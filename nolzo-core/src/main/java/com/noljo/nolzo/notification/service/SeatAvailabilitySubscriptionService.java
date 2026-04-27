package com.noljo.nolzo.notification.service;

import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.notification.application.port.in.CancelSeatAvailabilitySubscriptionUseCase;
import com.noljo.nolzo.notification.application.port.in.CreateSeatAvailabilitySubscriptionUseCase;
import com.noljo.nolzo.notification.application.port.in.GetSeatAvailabilitySubscriptionUseCase;
import com.noljo.nolzo.notification.application.port.out.LoadSeatAvailabilitySubscriptionPort;
import com.noljo.nolzo.notification.application.port.out.SaveSeatAvailabilitySubscriptionPort;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import com.noljo.nolzo.notification.dto.CreateSeatAvailabilitySubscriptionRequest;
import com.noljo.nolzo.notification.dto.SeatAvailabilitySubscriptionResponse;
import com.noljo.nolzo.schedule.application.port.out.SchedulePersistencePort;
import com.noljo.nolzo.schedule.entity.Schedule;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatAvailabilitySubscriptionService implements
        CreateSeatAvailabilitySubscriptionUseCase,
        CancelSeatAvailabilitySubscriptionUseCase,
        GetSeatAvailabilitySubscriptionUseCase {

    private final SaveSeatAvailabilitySubscriptionPort saveSeatAvailabilitySubscriptionPort;
    private final LoadSeatAvailabilitySubscriptionPort loadSeatAvailabilitySubscriptionPort;
    private final MemberPersistencePort memberPersistencePort;
    private final EventPersistencePort eventPersistencePort;
    private final SchedulePersistencePort schedulePersistencePort;

    @Override
    @Transactional
    public SeatAvailabilitySubscriptionResponse create(
            Long memberId,
            CreateSeatAvailabilitySubscriptionRequest request
    ) {
        validateCreate(memberId, request);

        SeatAvailabilitySubscription existing = loadSeatAvailabilitySubscriptionPort
                .findExistingSubscription(
                        memberId,
                        request.eventId(),
                        request.eventScheduleId(),
                        request.channel()
                )
                .orElse(null);

        if (existing != null) {
            if (existing.isActive()) {
                throw new IllegalArgumentException("이미 동일한 빈자리 알림을 구독 중입니다.");
            }

            existing.activate();
            return SeatAvailabilitySubscriptionResponse.from(
                    saveSeatAvailabilitySubscriptionPort.save(existing)
            );
        }

        SeatAvailabilitySubscription subscription = new SeatAvailabilitySubscription(
                memberId,
                request.eventId(),
                request.eventScheduleId(),
                request.channel(),
                SubscriptionStatus.ACTIVE
        );

        return SeatAvailabilitySubscriptionResponse.from(
                saveSeatAvailabilitySubscriptionPort.save(subscription)
        );
    }

    @Override
    @Transactional
    public void cancel(Long memberId, Long subscriptionId) {
        SeatAvailabilitySubscription subscription = loadSeatAvailabilitySubscriptionPort.findByIdAndMemberId(
                subscriptionId,
                memberId
        ).orElseThrow(() -> new IllegalArgumentException("해당 구독 정보가 없습니다."));

        if (!subscription.isActive()) {
            throw new IllegalArgumentException("이미 해지된 구독입니다.");
        }

        subscription.unsubscribe();
    }

    @Override
    public List<SeatAvailabilitySubscriptionResponse> readAllByMemberId(Long memberId) {
        return loadSeatAvailabilitySubscriptionPort.findMemberSubscriptions(
                        memberId,
                        SubscriptionStatus.ACTIVE
                ).stream()
                .map(SeatAvailabilitySubscriptionResponse::from)
                .toList();
    }

    private void validateCreate(Long memberId, CreateSeatAvailabilitySubscriptionRequest request) {
        memberPersistencePort.getOrThrow(memberId);
        eventPersistencePort.getOrThrow(request.eventId());

        Schedule schedule = schedulePersistencePort.findById(request.eventScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다."));

        if (!schedule.getEvent().getId().equals(request.eventId())) {
            throw new IllegalArgumentException("해당 이벤트에 속하지 않은 회차입니다.");
        }
    }
}
