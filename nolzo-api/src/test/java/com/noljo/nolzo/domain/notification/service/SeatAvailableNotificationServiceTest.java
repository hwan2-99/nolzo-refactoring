package com.noljo.nolzo.domain.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.notification.application.port.out.PublishNotificationBatchRequestPort;
import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import com.noljo.nolzo.notification.domain.event.NotificationBatchRequest;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import com.noljo.nolzo.notification.repository.SeatAvailabilitySubscriptionRepository;
import com.noljo.nolzo.notification.service.SeatAvailableNotificationService;
import com.noljo.nolzo.schedule.application.port.out.SchedulePersistencePort;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.MemberFixture;
import com.noljo.nolzo.support.fixture.ScheduleFixture;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

@ServiceTest
class SeatAvailableNotificationServiceTest {

    @Autowired
    private SeatAvailableNotificationService seatAvailableNotificationService;

    @Autowired
    private MemberPersistencePort memberPersistencePort;

    @Autowired
    private EventPersistencePort eventPersistencePort;

    @Autowired
    private SchedulePersistencePort schedulePersistencePort;

    @Autowired
    private SeatAvailabilitySubscriptionRepository seatAvailabilitySubscriptionRepository;

    @MockBean
    private PublishNotificationBatchRequestPort publishNotificationBatchRequestPort;

    @Test
    void 빈자리_이벤트를_처리하면_배치_요청을_발행한다() {
        Member first = memberPersistencePort.save(MemberFixture.회원());
        Member second = memberPersistencePort.save(MemberFixture.회투());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        SeatAvailabilitySubscription firstSubscription = seatAvailabilitySubscriptionRepository.save(
                new SeatAvailabilitySubscription(
                        first.getId(),
                        event.getId(),
                        schedule.getId(),
                        NotificationChannel.EMAIL,
                        SubscriptionStatus.ACTIVE
                )
        );
        SeatAvailabilitySubscription secondSubscription = seatAvailabilitySubscriptionRepository.save(
                new SeatAvailabilitySubscription(
                        second.getId(),
                        event.getId(),
                        schedule.getId(),
                        NotificationChannel.EMAIL,
                        SubscriptionStatus.ACTIVE
                )
        );

        SeatAvailableEvent seatAvailableEvent = new SeatAvailableEvent(
                event.getId(),
                schedule.getId(),
                100L,
                "1구역",
                LocalDateTime.of(2026, 4, 26, 12, 0)
        );

        seatAvailableNotificationService.handle(seatAvailableEvent);

        ArgumentCaptor<NotificationBatchRequest> captor = ArgumentCaptor.forClass(NotificationBatchRequest.class);
        verify(publishNotificationBatchRequestPort).publish(captor.capture());

        NotificationBatchRequest request = captor.getValue();
        assertThat(request.eventId()).isEqualTo(event.getId());
        assertThat(request.eventScheduleId()).isEqualTo(schedule.getId());
        assertThat(request.subscriptionIds())
                .containsExactlyInAnyOrder(firstSubscription.getId(), secondSubscription.getId());
    }

    @Test
    void 구독자가_없으면_배치_요청을_발행하지_않는다() {
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        seatAvailableNotificationService.handle(new SeatAvailableEvent(
                event.getId(),
                schedule.getId(),
                100L,
                "1구역",
                LocalDateTime.of(2026, 4, 26, 12, 0)
        ));

        verify(publishNotificationBatchRequestPort, never()).publish(any());
    }

    @Test
    void 배치_크기보다_구독자가_많으면_여러_개의_배치_요청으로_나눈다() {
        ReflectionTestUtils.setField(seatAvailableNotificationService, "batchSize", 1);

        Member first = memberPersistencePort.save(MemberFixture.회원());
        Member second = memberPersistencePort.save(MemberFixture.회투());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        seatAvailabilitySubscriptionRepository.save(new SeatAvailabilitySubscription(
                first.getId(),
                event.getId(),
                schedule.getId(),
                NotificationChannel.EMAIL,
                SubscriptionStatus.ACTIVE
        ));
        seatAvailabilitySubscriptionRepository.save(new SeatAvailabilitySubscription(
                second.getId(),
                event.getId(),
                schedule.getId(),
                NotificationChannel.EMAIL,
                SubscriptionStatus.ACTIVE
        ));

        seatAvailableNotificationService.handle(new SeatAvailableEvent(
                event.getId(),
                schedule.getId(),
                100L,
                "1구역",
                LocalDateTime.of(2026, 4, 26, 12, 0)
        ));

        ArgumentCaptor<NotificationBatchRequest> captor = ArgumentCaptor.forClass(NotificationBatchRequest.class);
        verify(publishNotificationBatchRequestPort, times(2)).publish(captor.capture());

        List<NotificationBatchRequest> requests = captor.getAllValues();
        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).subscriptionIds()).hasSize(1);
        assertThat(requests.get(1).subscriptionIds()).hasSize(1);
    }
}
