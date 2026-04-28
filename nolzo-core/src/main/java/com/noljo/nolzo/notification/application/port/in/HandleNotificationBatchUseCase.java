package com.noljo.nolzo.notification.application.port.in;

import com.noljo.nolzo.notification.domain.event.NotificationBatchRequest;

public interface HandleNotificationBatchUseCase {

    void handle(NotificationBatchRequest request);
}
