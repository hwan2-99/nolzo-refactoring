package com.noljo.nolzo.notification.adapter.out.persistence;

import com.noljo.nolzo.notification.application.port.out.LoadNotificationDeliveryPort;
import com.noljo.nolzo.notification.application.port.out.SaveNotificationDeliveryPort;
import com.noljo.nolzo.notification.domain.NotificationDelivery;
import com.noljo.nolzo.notification.domain.NotificationDeliveryStatus;
import com.noljo.nolzo.notification.repository.NotificationDeliveryRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationDeliveryPersistenceAdapter implements SaveNotificationDeliveryPort, LoadNotificationDeliveryPort {

    private final NotificationDeliveryRepository notificationDeliveryRepository;

    @Override
    public NotificationDelivery save(NotificationDelivery notificationDelivery) {
        return notificationDeliveryRepository.save(notificationDelivery);
    }

    @Override
    public boolean hasSentHistory(Long subscriptionId, LocalDateTime sentAt) {
        return notificationDeliveryRepository.existsSentHistory(
                subscriptionId,
                NotificationDeliveryStatus.SENT,
                sentAt
        );
    }
}
