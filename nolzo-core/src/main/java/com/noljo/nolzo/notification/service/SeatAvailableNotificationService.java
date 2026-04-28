package com.noljo.nolzo.notification.service;

import com.noljo.nolzo.notification.application.port.in.HandleSeatAvailableUseCase;
import com.noljo.nolzo.notification.application.port.out.LoadSeatAvailabilitySubscriptionPort;
import com.noljo.nolzo.notification.application.port.out.PublishNotificationBatchRequestPort;
import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import com.noljo.nolzo.notification.domain.event.NotificationBatchRequest;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatAvailableNotificationService implements HandleSeatAvailableUseCase {

    private final LoadSeatAvailabilitySubscriptionPort loadSeatAvailabilitySubscriptionPort;
    private final PublishNotificationBatchRequestPort publishNotificationBatchRequestPort;

    @Value("${app.notification.batch-size:100}")
    private int batchSize;

    @Override
    @Transactional
    public void handle(SeatAvailableEvent event) {
        processBatches(event);
    }

    private void processBatches(SeatAvailableEvent event) {
        int page = 0;

        while (true) {
            List<SeatAvailabilitySubscription> subscriptions = loadSubscriptions(event, page);

            if (subscriptions.isEmpty()) {
                return;
            }

            publishBatchRequest(event, subscriptions);

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

    private void publishBatchRequest(SeatAvailableEvent event, List<SeatAvailabilitySubscription> subscriptions) {
        List<Long> subscriptionIds = subscriptions.stream()
                .map(SeatAvailabilitySubscription::getId)
                .toList();

        publishNotificationBatchRequestPort.publish(new NotificationBatchRequest(
                event.eventId(),
                event.eventScheduleId(),
                event.seatId(),
                event.seatGrade(),
                event.availableAt(),
                subscriptionIds
        ));
    }
}
