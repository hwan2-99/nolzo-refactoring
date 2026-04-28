package com.noljo.nolzo.notification.service;

import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.notification.application.port.in.HandleNotificationBatchUseCase;
import com.noljo.nolzo.notification.application.port.out.LoadNotificationDeliveryPort;
import com.noljo.nolzo.notification.application.port.out.LoadSeatAvailabilitySubscriptionPort;
import com.noljo.nolzo.notification.application.port.out.SaveNotificationDeliveryPort;
import com.noljo.nolzo.notification.application.port.out.SendEmailNotificationPort;
import com.noljo.nolzo.notification.domain.NotificationDelivery;
import com.noljo.nolzo.notification.domain.NotificationSendResult;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.event.NotificationBatchRequest;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationBatchService implements HandleNotificationBatchUseCase {

    private final LoadSeatAvailabilitySubscriptionPort loadSeatAvailabilitySubscriptionPort;
    private final LoadNotificationDeliveryPort loadNotificationDeliveryPort;
    private final SaveNotificationDeliveryPort saveNotificationDeliveryPort;
    private final SendEmailNotificationPort sendEmailNotificationPort;
    private final MemberPersistencePort memberPersistencePort;
    private final EventPersistencePort eventPersistencePort;

    @Value("${app.notification.cooldown-minutes:10}")
    private int cooldownMinutes;

    @Value("${app.notification.retry-attempts:3}")
    private int retryAttempts;

    @Override
    @Transactional
    public void handle(NotificationBatchRequest request) {
        Event event = eventPersistencePort.getOrThrow(request.eventId());
        SeatAvailableEvent seatAvailableEvent = new SeatAvailableEvent(
                request.eventId(),
                request.eventScheduleId(),
                request.seatId(),
                request.seatGrade(),
                request.availableAt()
        );

        List<SeatAvailabilitySubscription> subscriptions =
                loadSeatAvailabilitySubscriptionPort.findSubscriptionsByIds(request.subscriptionIds());

        for (SeatAvailabilitySubscription subscription : subscriptions) {
            sendEmail(seatAvailableEvent, event, subscription);
        }
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
        NotificationSendResult result = sendWithRetry(recipient, targetEvent, event);

        if (result.success()) {
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

    private NotificationSendResult sendWithRetry(String recipient, Event targetEvent, SeatAvailableEvent event) {
        Exception failure = null;

        for (int attempt = 0; attempt < retryAttempts; attempt++) {
            try {
                sendEmailNotificationPort.send(
                        recipient,
                        createSubject(targetEvent),
                        createBody(targetEvent, event)
                );
                return NotificationSendResult.sent();
            } catch (Exception e) {
                failure = e;
            }
        }

        return NotificationSendResult.failed(
                failure == null ? "알 수 없는 발송 실패" : failure.getMessage()
        );
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
}
