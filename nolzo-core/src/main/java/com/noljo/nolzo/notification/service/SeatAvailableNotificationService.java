package com.noljo.nolzo.notification.service;

import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.notification.application.port.in.HandleSeatAvailableUseCase;
import com.noljo.nolzo.notification.application.port.out.LoadSeatAvailabilitySubscriptionPort;
import com.noljo.nolzo.notification.application.port.out.SaveNotificationDeliveryPort;
import com.noljo.nolzo.notification.application.port.out.SendEmailNotificationPort;
import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.NotificationDelivery;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatAvailableNotificationService implements HandleSeatAvailableUseCase {

    private final LoadSeatAvailabilitySubscriptionPort loadSeatAvailabilitySubscriptionPort;
    private final SaveNotificationDeliveryPort saveNotificationDeliveryPort;
    private final SendEmailNotificationPort sendEmailNotificationPort;
    private final MemberPersistencePort memberPersistencePort;
    private final EventPersistencePort eventPersistencePort;

    @Override
    @Transactional
    public void handle(SeatAvailableEvent event) {
        List<SeatAvailabilitySubscription> subscriptions = loadSeatAvailabilitySubscriptionPort.findTargetSubscriptions(
                event.eventId(),
                event.eventScheduleId(),
                SubscriptionStatus.ACTIVE,
                NotificationChannel.EMAIL
        );

        if (subscriptions.isEmpty()) {
            return;
        }

        Event targetEvent = eventPersistencePort.getOrThrow(event.eventId());

        for (SeatAvailabilitySubscription subscription : subscriptions) {
            sendEmail(event, targetEvent, subscription);
        }
    }

    private void sendEmail(
            SeatAvailableEvent event,
            Event targetEvent,
            SeatAvailabilitySubscription subscription
    ) {
        String recipient = getRecipient(subscription);
        try {
            sendEmailNotificationPort.send(
                    recipient,
                    createSubject(targetEvent),
                    createBody(targetEvent, event)
            );
            saveSuccess(event, subscription, recipient);
        } catch (Exception e) {
            saveFailure(event, subscription, recipient, e.getMessage());
        }
    }

    private String getRecipient(SeatAvailabilitySubscription subscription) {
        Member member = memberPersistencePort.getOrThrow(subscription.getMemberId());
        return member.getEmail();
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
