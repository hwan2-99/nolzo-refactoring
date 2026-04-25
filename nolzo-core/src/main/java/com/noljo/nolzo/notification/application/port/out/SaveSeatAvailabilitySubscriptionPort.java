package com.noljo.nolzo.notification.application.port.out;

import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;

public interface SaveSeatAvailabilitySubscriptionPort {

    SeatAvailabilitySubscription save(SeatAvailabilitySubscription subscription);
}
