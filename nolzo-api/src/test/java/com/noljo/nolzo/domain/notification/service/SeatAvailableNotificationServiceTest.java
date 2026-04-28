package com.noljo.nolzo.domain.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.notification.application.port.out.SendEmailNotificationPort;
import com.noljo.nolzo.notification.domain.NotificationDelivery;
import com.noljo.nolzo.notification.domain.NotificationDeliveryStatus;
import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import com.noljo.nolzo.notification.repository.NotificationDeliveryRepository;
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

    @Autowired
    private NotificationDeliveryRepository notificationDeliveryRepository;

    @MockBean
    private SendEmailNotificationPort sendEmailNotificationPort;

    @Test
    void 빈자리_이벤트를_처리하면_구독자에게_알림을_발송하고_이력을_저장한다() {
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        seatAvailabilitySubscriptionRepository.save(new SeatAvailabilitySubscription(
                member.getId(),
                event.getId(),
                schedule.getId(),
                NotificationChannel.EMAIL,
                SubscriptionStatus.ACTIVE
        ));

        SeatAvailableEvent seatAvailableEvent = new SeatAvailableEvent(
                event.getId(),
                schedule.getId(),
                100L,
                "1구역",
                LocalDateTime.of(2026, 4, 26, 12, 0)
        );

        seatAvailableNotificationService.handle(seatAvailableEvent);

        verify(sendEmailNotificationPort).send(
                member.getEmail(),
                "[NOLZO] " + event.getTitle() + " 빈자리가 발생했습니다.",
                """
                %s 공연에 빈자리가 발생했습니다.
                회차 ID: %d
                좌석 등급: %s
                빠르게 예매를 시도해 주세요.
                """.formatted(event.getTitle(), schedule.getId(), "1구역")
        );

        List<NotificationDelivery> deliveries = notificationDeliveryRepository.findAll();
        assertThat(deliveries).hasSize(1);
        assertThat(deliveries.get(0).getStatus()).isEqualTo(NotificationDeliveryStatus.SENT);
        assertThat(deliveries.get(0).getRecipient()).isEqualTo(member.getEmail());
    }

    @Test
    void 구독자가_없으면_알림_이력이_생기지_않는다() {
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        seatAvailableNotificationService.handle(new SeatAvailableEvent(
                event.getId(),
                schedule.getId(),
                100L,
                "1구역",
                LocalDateTime.of(2026, 4, 26, 12, 0)
        ));

        assertThat(notificationDeliveryRepository.findAll()).isEmpty();
    }

    @Test
    void 동일한_이벤트가_재처리되어도_중복_발송하지_않는다() {
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        seatAvailabilitySubscriptionRepository.save(new SeatAvailabilitySubscription(
                member.getId(),
                event.getId(),
                schedule.getId(),
                NotificationChannel.EMAIL,
                SubscriptionStatus.ACTIVE
        ));

        SeatAvailableEvent seatAvailableEvent = new SeatAvailableEvent(
                event.getId(),
                schedule.getId(),
                100L,
                "1구역",
                LocalDateTime.of(2026, 4, 26, 12, 0)
        );

        seatAvailableNotificationService.handle(seatAvailableEvent);
        seatAvailableNotificationService.handle(seatAvailableEvent);

        verify(sendEmailNotificationPort, times(1)).send(anyString(), anyString(), anyString());
        assertThat(notificationDeliveryRepository.findAll()).hasSize(1);
    }

    @Test
    void 배치_크기보다_구독자가_많아도_나누어_처리한다() {
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

        verify(sendEmailNotificationPort, times(2)).send(anyString(), anyString(), anyString());
        assertThat(notificationDeliveryRepository.findAll()).hasSize(2);
    }

    @Test
    void 이메일_발송에_실패하면_설정된_횟수만큼_재시도한다() {
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        seatAvailabilitySubscriptionRepository.save(new SeatAvailabilitySubscription(
                member.getId(),
                event.getId(),
                schedule.getId(),
                NotificationChannel.EMAIL,
                SubscriptionStatus.ACTIVE
        ));

        doThrow(new RuntimeException("mail send failed"))
                .when(sendEmailNotificationPort)
                .send(anyString(), anyString(), anyString());

        seatAvailableNotificationService.handle(new SeatAvailableEvent(
                event.getId(),
                schedule.getId(),
                100L,
                "1구역",
                LocalDateTime.of(2026, 4, 26, 12, 0)
        ));

        verify(sendEmailNotificationPort, times(3)).send(anyString(), anyString(), anyString());
        assertThat(notificationDeliveryRepository.findAll()).hasSize(1);
        assertThat(notificationDeliveryRepository.findAll().get(0).getStatus())
                .isEqualTo(NotificationDeliveryStatus.FAILED);
    }
}
