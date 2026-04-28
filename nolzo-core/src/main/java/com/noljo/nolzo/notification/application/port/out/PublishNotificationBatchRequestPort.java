package com.noljo.nolzo.notification.application.port.out;

import com.noljo.nolzo.notification.domain.event.NotificationBatchRequest;

public interface PublishNotificationBatchRequestPort {

    void publish(NotificationBatchRequest request);
}
