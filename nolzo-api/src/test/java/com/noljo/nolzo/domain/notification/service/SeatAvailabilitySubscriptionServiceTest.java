package com.noljo.nolzo.domain.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import com.noljo.nolzo.notification.dto.CreateSeatAvailabilitySubscriptionRequest;
import com.noljo.nolzo.notification.dto.SeatAvailabilitySubscriptionResponse;
import com.noljo.nolzo.notification.repository.SeatAvailabilitySubscriptionRepository;
import com.noljo.nolzo.notification.service.SeatAvailabilitySubscriptionService;
import com.noljo.nolzo.schedule.application.port.out.SchedulePersistencePort;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.MemberFixture;
import com.noljo.nolzo.support.fixture.ScheduleFixture;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
class SeatAvailabilitySubscriptionServiceTest {

    @Autowired
    private SeatAvailabilitySubscriptionService seatAvailabilitySubscriptionService;

    @Autowired
    private SeatAvailabilitySubscriptionRepository seatAvailabilitySubscriptionRepository;

    @Autowired
    private MemberPersistencePort memberPersistencePort;

    @Autowired
    private EventPersistencePort eventPersistencePort;

    @Autowired
    private SchedulePersistencePort schedulePersistencePort;

    @Test
    void 빈자리_알림_구독을_생성할_수_있다() {
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        SeatAvailabilitySubscriptionResponse response = seatAvailabilitySubscriptionService.create(
                member.getId(),
                request(event.getId(), schedule.getId())
        );

        assertThat(response.getMemberId()).isEqualTo(member.getId());
        assertThat(response.getEventId()).isEqualTo(event.getId());
        assertThat(response.getEventScheduleId()).isEqualTo(schedule.getId());
        assertThat(response.getChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(response.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    void 동일한_빈자리_알림을_이미_구독중이면_예외가_발생한다() {
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));
        CreateSeatAvailabilitySubscriptionRequest request =
                request(event.getId(), schedule.getId());

        seatAvailabilitySubscriptionService.create(member.getId(), request);

        assertThatThrownBy(() -> seatAvailabilitySubscriptionService.create(member.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 동일한 빈자리 알림을 구독 중입니다.");
    }

    @Test
    void 구독을_해지할_수_있다() {
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        SeatAvailabilitySubscriptionResponse response = seatAvailabilitySubscriptionService.create(
                member.getId(),
                request(event.getId(), schedule.getId())
        );

        seatAvailabilitySubscriptionService.cancel(member.getId(), response.getId());

        SeatAvailabilitySubscription subscription = seatAvailabilitySubscriptionRepository.findById(response.getId())
                .orElseThrow();
        assertThat(subscription.getStatus()).isEqualTo(SubscriptionStatus.UNSUBSCRIBED);
    }

    @Test
    void 해지된_구독은_같은_조건으로_다시_구독하면_재활성화된다() {
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));
        CreateSeatAvailabilitySubscriptionRequest request =
                request(event.getId(), schedule.getId());

        SeatAvailabilitySubscriptionResponse created = seatAvailabilitySubscriptionService.create(member.getId(), request);
        seatAvailabilitySubscriptionService.cancel(member.getId(), created.getId());

        SeatAvailabilitySubscriptionResponse recreated = seatAvailabilitySubscriptionService.create(member.getId(), request);

        List<SeatAvailabilitySubscription> subscriptions = seatAvailabilitySubscriptionRepository.findAll();
        assertThat(subscriptions).hasSize(1);
        assertThat(recreated.getId()).isEqualTo(created.getId());
        assertThat(subscriptions.get(0).getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    private CreateSeatAvailabilitySubscriptionRequest request(Long eventId, Long scheduleId) {
        return new CreateSeatAvailabilitySubscriptionRequest(
                eventId,
                scheduleId,
                NotificationChannel.EMAIL
        );
    }
}
