package com.noljo.nolzo.notification.repository;

import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeatAvailabilitySubscriptionRepository extends JpaRepository<SeatAvailabilitySubscription, Long> {

    @Query("""
            select s
            from SeatAvailabilitySubscription s
            where s.memberId = :memberId
              and s.eventId = :eventId
              and s.eventScheduleId = :eventScheduleId
              and s.channel = :channel
            """)
    Optional<SeatAvailabilitySubscription> findExistingSubscription(
            @Param("memberId") Long memberId,
            @Param("eventId") Long eventId,
            @Param("eventScheduleId") Long eventScheduleId,
            @Param("channel") NotificationChannel channel
    );

    @Query("""
            select s
            from SeatAvailabilitySubscription s
            where s.memberId = :memberId
              and s.status = :status
            order by s.createdAt desc
            """)
    List<SeatAvailabilitySubscription> findMemberSubscriptions(
            @Param("memberId") Long memberId,
            @Param("status") SubscriptionStatus status
    );

    @Query("""
            select s
            from SeatAvailabilitySubscription s
            where s.eventId = :eventId
              and s.eventScheduleId = :eventScheduleId
              and s.status = :status
              and s.channel = :channel
            order by s.createdAt asc
            """)
    List<SeatAvailabilitySubscription> findTargetSubscriptions(
            @Param("eventId") Long eventId,
            @Param("eventScheduleId") Long eventScheduleId,
            @Param("status") SubscriptionStatus status,
            @Param("channel") NotificationChannel channel
    );

    Optional<SeatAvailabilitySubscription> findByIdAndMemberId(Long id, Long memberId);
}
