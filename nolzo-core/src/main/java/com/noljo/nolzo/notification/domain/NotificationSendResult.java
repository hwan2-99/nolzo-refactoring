package com.noljo.nolzo.notification.domain;

public record NotificationSendResult(
        boolean success,
        String failureReason
) {

    public static NotificationSendResult sent() {
        return new NotificationSendResult(true, null);
    }

    public static NotificationSendResult failed(String failureReason) {
        return new NotificationSendResult(false, failureReason);
    }
}
