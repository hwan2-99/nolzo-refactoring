package com.noljo.nolzo.notification.adapter.out.persistence;

import com.noljo.nolzo.notification.application.port.out.SaveNotificationDeliveryPort;
import com.noljo.nolzo.notification.domain.NotificationDelivery;
import com.noljo.nolzo.notification.repository.NotificationDeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationDeliveryPersistenceAdapter implements SaveNotificationDeliveryPort {

    private final NotificationDeliveryRepository notificationDeliveryRepository;

    @Override
    public NotificationDelivery save(NotificationDelivery notificationDelivery) {
        return notificationDeliveryRepository.save(notificationDelivery);
    }
}
