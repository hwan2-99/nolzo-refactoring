package com.noljo.nolzo.notification.adapter.out.persistence;

import com.noljo.nolzo.notification.application.port.out.LoadSeatAvailabilitySubscriptionPort;
import com.noljo.nolzo.notification.application.port.out.SaveSeatAvailabilitySubscriptionPort;
import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import com.noljo.nolzo.notification.repository.SeatAvailabilitySubscriptionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
            NotificationChannel channel
    ) {
        return seatAvailabilitySubscriptionRepository.findExistingSubscription(
                memberId,
                eventId,
                eventScheduleId,
                channel
        );
    }

    @Override
    public List<SeatAvailabilitySubscription> findMemberSubscriptions(
            Long memberId,
            SubscriptionStatus status
    ) {
        return seatAvailabilitySubscriptionRepository.findMemberSubscriptions(
                memberId,
                status
        );
    }

    @Override
    public List<SeatAvailabilitySubscription> findTargetSubscriptions(
            Long eventId,
            Long eventScheduleId,
            SubscriptionStatus status,
            NotificationChannel channel,
            int page,
            int size
    ) {
        return seatAvailabilitySubscriptionRepository.findTargetSubscriptions(
                eventId,
                eventScheduleId,
                status,
                channel,
                PageRequest.of(page, size)
        );
    }

    @Override
    public List<SeatAvailabilitySubscription> findSubscriptionsByIds(List<Long> subscriptionIds) {
        return seatAvailabilitySubscriptionRepository.findSubscriptionsByIds(subscriptionIds);
    }

    @Override
    public Optional<SeatAvailabilitySubscription> findByIdAndMemberId(Long id, Long memberId) {
        return seatAvailabilitySubscriptionRepository.findByIdAndMemberId(id, memberId);
    }
}
