package com.noljo.nolzo.notification.application.port.out;

import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import java.util.List;
import java.util.Optional;

public interface LoadSeatAvailabilitySubscriptionPort {

    Optional<SeatAvailabilitySubscription> findExistingSubscription(
            Long memberId,
            Long eventId,
            Long eventScheduleId,
            NotificationChannel channel
    );

    List<SeatAvailabilitySubscription> findMemberSubscriptions(
            Long memberId,
            SubscriptionStatus status
    );

    List<SeatAvailabilitySubscription> findTargetSubscriptions(
            Long eventId,
            Long eventScheduleId,
            SubscriptionStatus status,
            NotificationChannel channel
    );

    Optional<SeatAvailabilitySubscription> findByIdAndMemberId(Long id, Long memberId);
}
