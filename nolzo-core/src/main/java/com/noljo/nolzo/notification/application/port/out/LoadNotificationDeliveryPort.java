package com.noljo.nolzo.notification.application.port.out;

import java.time.LocalDateTime;

public interface LoadNotificationDeliveryPort {

    boolean hasSentHistory(Long subscriptionId, LocalDateTime sentAt);
}
