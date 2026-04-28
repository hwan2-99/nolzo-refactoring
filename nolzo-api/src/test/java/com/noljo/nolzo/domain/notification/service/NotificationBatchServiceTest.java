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
import com.noljo.nolzo.notification.domain.NotificationChannel;
import com.noljo.nolzo.notification.domain.NotificationDelivery;
import com.noljo.nolzo.notification.domain.NotificationDeliveryStatus;
import com.noljo.nolzo.notification.domain.SeatAvailabilitySubscription;
import com.noljo.nolzo.notification.domain.SubscriptionStatus;
import com.noljo.nolzo.notification.domain.event.NotificationBatchRequest;
import com.noljo.nolzo.notification.repository.NotificationDeliveryRepository;
import com.noljo.nolzo.notification.repository.SeatAvailabilitySubscriptionRepository;
import com.noljo.nolzo.notification.service.NotificationBatchService;
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

@ServiceTest
class NotificationBatchServiceTest {

    @Autowired
    private NotificationBatchService notificationBatchService;

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
    void 배치_요청을_처리하면_구독자에게_알림을_발송하고_이력을_저장한다() {
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        SeatAvailabilitySubscription subscription = seatAvailabilitySubscriptionRepository.save(
                new SeatAvailabilitySubscription(
                        member.getId(),
                        event.getId(),
                        schedule.getId(),
                        NotificationChannel.EMAIL,
                        SubscriptionStatus.ACTIVE
                )
        );

        NotificationBatchRequest request = new NotificationBatchRequest(
                event.getId(),
                schedule.getId(),
                100L,
                "1구역",
                LocalDateTime.of(2026, 4, 26, 12, 0),
                List.of(subscription.getId())
        );

        notificationBatchService.handle(request);

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
    void 동일한_배치_요청이_재처리되어도_중복_발송하지_않는다() {
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        SeatAvailabilitySubscription subscription = seatAvailabilitySubscriptionRepository.save(
                new SeatAvailabilitySubscription(
                        member.getId(),
                        event.getId(),
                        schedule.getId(),
                        NotificationChannel.EMAIL,
                        SubscriptionStatus.ACTIVE
                )
        );

        NotificationBatchRequest request = new NotificationBatchRequest(
                event.getId(),
                schedule.getId(),
                100L,
                "1구역",
                LocalDateTime.of(2026, 4, 26, 12, 0),
                List.of(subscription.getId())
        );

        notificationBatchService.handle(request);
        notificationBatchService.handle(request);

        verify(sendEmailNotificationPort, times(1)).send(anyString(), anyString(), anyString());
        assertThat(notificationDeliveryRepository.findAll()).hasSize(1);
    }

    @Test
    void 이메일_발송에_실패하면_설정된_횟수만큼_재시도한다() {
        Member member = memberPersistencePort.save(MemberFixture.회원());
        Event event = eventPersistencePort.save(EventFixture.캣츠());
        Schedule schedule = schedulePersistencePort.save(ScheduleFixture.공연_스케쥴(event));

        SeatAvailabilitySubscription subscription = seatAvailabilitySubscriptionRepository.save(
                new SeatAvailabilitySubscription(
                        member.getId(),
                        event.getId(),
                        schedule.getId(),
                        NotificationChannel.EMAIL,
                        SubscriptionStatus.ACTIVE
                )
        );

        doThrow(new RuntimeException("mail send failed"))
                .when(sendEmailNotificationPort)
                .send(anyString(), anyString(), anyString());

        notificationBatchService.handle(new NotificationBatchRequest(
                event.getId(),
                schedule.getId(),
                100L,
                "1구역",
                LocalDateTime.of(2026, 4, 26, 12, 0),
                List.of(subscription.getId())
        ));

        verify(sendEmailNotificationPort, times(3)).send(anyString(), anyString(), anyString());
        assertThat(notificationDeliveryRepository.findAll()).hasSize(1);
        assertThat(notificationDeliveryRepository.findAll().get(0).getStatus())
                .isEqualTo(NotificationDeliveryStatus.FAILED);
    }
}
