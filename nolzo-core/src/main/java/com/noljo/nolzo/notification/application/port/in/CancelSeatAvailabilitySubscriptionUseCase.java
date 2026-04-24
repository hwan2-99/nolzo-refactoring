package com.noljo.nolzo.notification.application.port.in;

public interface CancelSeatAvailabilitySubscriptionUseCase {

    void cancel(Long memberId, Long subscriptionId);
}
