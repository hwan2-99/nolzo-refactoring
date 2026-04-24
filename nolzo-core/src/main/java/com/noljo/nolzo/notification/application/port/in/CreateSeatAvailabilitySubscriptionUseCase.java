package com.noljo.nolzo.notification.application.port.in;

import com.noljo.nolzo.notification.dto.CreateSeatAvailabilitySubscriptionRequest;
import com.noljo.nolzo.notification.dto.SeatAvailabilitySubscriptionResponse;

public interface CreateSeatAvailabilitySubscriptionUseCase {

    SeatAvailabilitySubscriptionResponse create(
            Long memberId,
            CreateSeatAvailabilitySubscriptionRequest request
    );
}
