package com.noljo.nolzo.domain.payment.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.payment.dto.PaymentRequest;
import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.payment.entity.PaymentMethod;
import com.noljo.nolzo.payment.repository.PaymentRepository;
import com.noljo.nolzo.payment.service.PaymentService;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.schedule.repository.ScheduleRepository;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.repository.SeatRepository;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.MemberFixture;
import com.noljo.nolzo.support.fixture.PaymentFixture;
import com.noljo.nolzo.support.fixture.ReservationFixture;
import com.noljo.nolzo.support.fixture.ScheduleFixture;
import com.noljo.nolzo.support.fixture.SeatFixture;
import com.noljo.nolzo.support.fixture.TicketFixture;
import com.noljo.nolzo.ticket.entity.Ticket;
import com.noljo.nolzo.ticket.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
public class PaymentServiceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentService paymentService;

    @Test
    void 예약과_유저를_통해_결제를_할_수_있다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);

        Reservation reservation = ReservationFixture.예약(member);
        reservationRepository.save(reservation);

        paymentRepository.save(PaymentFixture.신용카드(member, reservation));
        assertThat(paymentRepository.findAll()).hasSize(1);
    }

    @Test
    void 결제_취소_시_객체는_생성되지_않는다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);

        Reservation reservation = ReservationFixture.예약(member);
        reservationRepository.save(reservation);
        PaymentRequest request = new PaymentRequest(reservation.getId(), PaymentMethod.CASH,
                "CANCELED");
        paymentService.create(member.getId(), request);

        assertThat(paymentRepository.findAll()).hasSize(0);
        assertThat(reservationRepository.findAll()).hasSize(0);
    }

    @Test
    void 결제_취소_시_예약과_티켓은_생성되지_않는다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);

        Event event = EventFixture.캣츠();
        eventRepository.save(event);

        Schedule schedule = ScheduleFixture.공연_스케쥴(event);
        scheduleRepository.save(schedule);

        Seat seat = SeatFixture.일반좌석(schedule);
        seatRepository.save(seat);

        Reservation reservation = ReservationFixture.예약(member);
        reservationRepository.save(reservation);

        Ticket ticket = TicketFixture.미사용티켓(reservation, seat);
        ticketRepository.save(ticket);

        PaymentRequest request = new PaymentRequest(reservation.getId(), PaymentMethod.CASH,
                "CANCELED");
        paymentService.create(member.getId(), request);

        assertThat(reservationRepository.findAll()).hasSize(0);
        assertThat(ticketRepository.findAll()).hasSize(0);
    }
}
