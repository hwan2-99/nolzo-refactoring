package com.noljo.nolzo.domain.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.dto.ReservationRequest;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.reservation.service.ReservationService;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.repository.SeatRepository;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.MemberFixture;
import com.noljo.nolzo.support.fixture.SeatFixture;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
class ReservationServiceTest {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SeatRepository seatRepository;

    @Test
    void 같은_좌석은_동시에_접근이_불가능하다() throws InterruptedException {
        Member member = MemberFixture.회원();
        memberRepository.save(member);
        Member anotherMember = MemberFixture.회투();
        memberRepository.save(anotherMember);

        Event event = EventFixture.캣츠();
        eventRepository.save(event);

        Seat seat = SeatFixture.일반좌석(event);
        seatRepository.save(seat);
        Seat seat2 = SeatFixture.일반좌석2(event);
        seatRepository.save(seat2);

        List<Seat> seats = seatRepository.findAll();
        ReservationRequest request = new ReservationRequest(event.getId(), seats);

        Thread thread1 = new Thread(() -> reservationService.create(member.getId(), request));
        Thread thread2 = new Thread(() -> reservationService.create(anotherMember.getId(), request));

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        assertThatThrownBy(() -> reservationService.create(anotherMember.getId(),
                new ReservationRequest(event.getId(), seatRepository.findAll())))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
