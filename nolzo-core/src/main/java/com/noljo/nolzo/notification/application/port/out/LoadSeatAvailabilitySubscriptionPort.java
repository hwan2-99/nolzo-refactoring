package com.noljo.nolzo.notification.application.port.out;

import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import com.noljo.nolzo.seat.entity.SectionPrice;
import java.util.List;
import java.util.Optional;

public interface LoadSeatAvailabilitySubscriptionPort {

    Optional<SeatAvailabilitySubscription> findExistingSubscription(
            Long memberId,
            Long eventId,
            Long eventScheduleId,
            SectionPrice seatGrade,
            NotificationChannel channel
    );

    List<SeatAvailabilitySubscription> findAllByMemberIdAndStatus(
            Long memberId,
            SubscriptionStatus status
    );

    List<SeatAvailabilitySubscription> findAllByEventScheduleSeatGradeAndStatusAndChannel(
            Long eventId,
            Long eventScheduleId,
            SectionPrice seatGrade,
            SubscriptionStatus status,
            NotificationChannel channel
    );

    Optional<SeatAvailabilitySubscription> findByIdAndMemberId(Long id, Long memberId);
}
