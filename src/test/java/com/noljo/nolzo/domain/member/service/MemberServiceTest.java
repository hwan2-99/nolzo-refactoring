package com.noljo.nolzo.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.seat.repository.SeatRepository;
import com.noljo.nolzo.support.fixture.MemberFixture;
import com.noljo.nolzo.ticket.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberServiceTest {
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

    @BeforeEach
    void setUp() {
        //추후 어노테이션으로 처리예정
        ticketRepository.deleteAll();
        reservationRepository.deleteAll();
        seatRepository.deleteAll();
        eventRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void 회원은_저장_가능하다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);
        assertThat(memberRepository.findAll()).hasSize(1);
    }
}
