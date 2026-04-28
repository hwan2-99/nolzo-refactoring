package com.noljo.nolzo.notification.service;

import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.notification.application.port.in.HandleSeatAvailableUseCase;
import com.noljo.nolzo.notification.application.port.out.LoadNotificationDeliveryPort;
import com.noljo.nolzo.notification.application.port.out.LoadSeatAvailabilitySubscriptionPort;
import com.noljo.nolzo.notification.application.port.out.SaveNotificationDeliveryPort;
import com.noljo.nolzo.notification.application.port.out.SendEmailNotificationPort;
import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.NotificationDelivery;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatAvailableNotificationService implements HandleSeatAvailableUseCase {

    private final LoadSeatAvailabilitySubscriptionPort loadSeatAvailabilitySubscriptionPort;
    private final LoadNotificationDeliveryPort loadNotificationDeliveryPort;
    private final SaveNotificationDeliveryPort saveNotificationDeliveryPort;
    private final SendEmailNotificationPort sendEmailNotificationPort;
    private final MemberPersistencePort memberPersistencePort;
    private final EventPersistencePort eventPersistencePort;

    @Value("${app.notification.batch-size:100}")
    private int batchSize;

    @Value("${app.notification.cooldown-minutes:10}")
    private int cooldownMinutes;

    @Value("${app.notification.retry-attempts:3}")
    private int retryAttempts;

    @Override
    @Transactional
    public void handle(SeatAvailableEvent event) {
        Event targetEvent = eventPersistencePort.getOrThrow(event.eventId());
        processBatches(event, targetEvent);
    }

    private void processBatches(SeatAvailableEvent event, Event targetEvent) {
        int page = 0;

        while (true) {
            List<SeatAvailabilitySubscription> subscriptions = loadSubscriptions(event, page);

            if (subscriptions.isEmpty()) {
                return;
            }

            for (SeatAvailabilitySubscription subscription : subscriptions) {
                sendEmail(event, targetEvent, subscription);
            }

            if (subscriptions.size() < batchSize) {
                return;
            }

            page++;
        }
    }

    private List<SeatAvailabilitySubscription> loadSubscriptions(SeatAvailableEvent event, int page) {
        return loadSeatAvailabilitySubscriptionPort.findTargetSubscriptions(
                event.eventId(),
                event.eventScheduleId(),
                SubscriptionStatus.ACTIVE,
                NotificationChannel.EMAIL,
                page,
                batchSize
        );
    }

    private void sendEmail(
            SeatAvailableEvent event,
            Event targetEvent,
            SeatAvailabilitySubscription subscription
    ) {
        if (shouldSkipNotification(subscription, event.availableAt())) {
            return;
        }

        String recipient = getRecipient(subscription);
        SendResult result = sendWithRetry(recipient, targetEvent, event);

        if (result.isSuccess()) {
            saveSuccess(event, subscription, recipient);
            return;
        }

        saveFailure(event, subscription, recipient, result.failureReason());
    }

    private String getRecipient(SeatAvailabilitySubscription subscription) {
        Member member = memberPersistencePort.getOrThrow(subscription.getMemberId());
        return member.getEmail();
    }

    private boolean shouldSkipNotification(SeatAvailabilitySubscription subscription, LocalDateTime availableAt) {
        return alreadySent(subscription, availableAt) || isCooldownActive(subscription, availableAt);
    }

    private SendResult sendWithRetry(String recipient, Event targetEvent, SeatAvailableEvent event) {
        Exception failure = null;

        for (int attempt = 0; attempt < retryAttempts; attempt++) {
            try {
                sendEmailNotificationPort.send(
                        recipient,
                        createSubject(targetEvent),
                        createBody(targetEvent, event)
                );
                return SendResult.sent();
            } catch (Exception e) {
                failure = e;
            }
        }

        return SendResult.failed(failure == null ? "알 수 없는 발송 실패" : failure.getMessage());
    }

    private void saveSuccess(
            SeatAvailableEvent event,
            SeatAvailabilitySubscription subscription,
            String recipient
    ) {
        subscription.updateLastNotifiedAt(event.availableAt());
        saveNotificationDeliveryPort.save(new NotificationDelivery(
                subscription.getId(),
                subscription.getMemberId(),
                event.eventId(),
                event.eventScheduleId(),
                event.seatGrade(),
                subscription.getChannel(),
                recipient,
                event.availableAt()
        ));
    }

    private void saveFailure(
            SeatAvailableEvent event,
            SeatAvailabilitySubscription subscription,
            String recipient,
            String failureReason
    ) {
        saveNotificationDeliveryPort.save(new NotificationDelivery(
                subscription.getId(),
                subscription.getMemberId(),
                event.eventId(),
                event.eventScheduleId(),
                event.seatGrade(),
                subscription.getChannel(),
                recipient,
                failureReason
        ));
    }

    private boolean alreadySent(SeatAvailabilitySubscription subscription, LocalDateTime availableAt) {
        return loadNotificationDeliveryPort.hasSentHistory(subscription.getId(), availableAt);
    }

    private boolean isCooldownActive(SeatAvailabilitySubscription subscription, LocalDateTime availableAt) {
        LocalDateTime lastNotifiedAt = subscription.getLastNotifiedAt();

        if (lastNotifiedAt == null) {
            return false;
        }

        return !availableAt.isAfter(lastNotifiedAt.plusMinutes(cooldownMinutes));
    }

    private String createSubject(Event event) {
        return "[NOLZO] " + event.getTitle() + " 빈자리가 발생했습니다.";
    }

    private String createBody(Event event, SeatAvailableEvent seatAvailableEvent) {
        return """
                %s 공연에 빈자리가 발생했습니다.
                회차 ID: %d
                좌석 등급: %s
                빠르게 예매를 시도해 주세요.
                """.formatted(
                event.getTitle(),
                seatAvailableEvent.eventScheduleId(),
                seatAvailableEvent.seatGrade()
        );
    }

    private record SendResult(boolean success, String failureReason) {

        private boolean isSuccess() {
            return success;
        }

        private static SendResult sent() {
            return new SendResult(true, null);
        }

        private static SendResult failed(String failureReason) {
            return new SendResult(false, failureReason);
        }
    }
}
