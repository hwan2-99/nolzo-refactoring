package com.noljo.nolzo.notification.application.port.out;

import com.noljo.nolzo.notification.domain.NotificationDelivery;

public interface SaveNotificationDeliveryPort {

    NotificationDelivery save(NotificationDelivery notificationDelivery);
}
