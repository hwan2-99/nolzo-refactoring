package com.noljo.nolzo.notification.adapter.out.persistence;

import com.noljo.nolzo.notification.application.port.out.LoadSeatAvailabilitySubscriptionPort;
import com.noljo.nolzo.notification.application.port.out.SaveSeatAvailabilitySubscriptionPort;
import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import com.noljo.nolzo.notification.repository.SeatAvailabilitySubscriptionRepository;
import com.noljo.nolzo.seat.entity.SectionPrice;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatAvailabilitySubscriptionPersistenceAdapter implements
        SaveSeatAvailabilitySubscriptionPort,
        LoadSeatAvailabilitySubscriptionPort {

    private final SeatAvailabilitySubscriptionRepository seatAvailabilitySubscriptionRepository;

    @Override
    public SeatAvailabilitySubscription save(SeatAvailabilitySubscription subscription) {
        return seatAvailabilitySubscriptionRepository.save(subscription);
    }

    @Override
    public Optional<SeatAvailabilitySubscription> findExistingSubscription(
            Long memberId,
            Long eventId,
            Long eventScheduleId,
            SectionPrice seatGrade,
            NotificationChannel channel
    ) {
        return seatAvailabilitySubscriptionRepository.findExistingSubscription(
                memberId,
                eventId,
                eventScheduleId,
                seatGrade,
                channel
        );
    }

    @Override
    public List<SeatAvailabilitySubscription> findAllByMemberIdAndStatus(
            Long memberId,
            SubscriptionStatus status
    ) {
        return seatAvailabilitySubscriptionRepository.findAllByMemberIdAndStatus(
                memberId,
                status
        );
    }

    @Override
    public List<SeatAvailabilitySubscription> findAllByEventScheduleSeatGradeAndStatusAndChannel(
            Long eventId,
            Long eventScheduleId,
            SectionPrice seatGrade,
            SubscriptionStatus status,
            NotificationChannel channel
    ) {
        return seatAvailabilitySubscriptionRepository.findAllByEventScheduleSeatGradeAndStatusAndChannel(
                eventId,
                eventScheduleId,
                seatGrade,
                status,
                channel
        );
    }

    @Override
    public Optional<SeatAvailabilitySubscription> findByIdAndMemberId(Long id, Long memberId) {
        return seatAvailabilitySubscriptionRepository.findByIdAndMemberId(id, memberId);
    }
}
