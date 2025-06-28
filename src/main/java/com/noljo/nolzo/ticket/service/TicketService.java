package com.noljo.nolzo.ticket.service;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.ticket.dto.TicketResponse;
import com.noljo.nolzo.ticket.entity.Ticket;
import com.noljo.nolzo.ticket.entity.TicketStatus;
import com.noljo.nolzo.ticket.repository.TicketRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class TicketService {
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;

    public TicketResponse create(Reservation reservation, Seat seat) {
        Ticket ticket = new Ticket(TicketStatus.NOT_USED, reservation, seat);
        ticketRepository.save(ticket);
        return TicketResponse.from(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> findTickets(Long memberId) {
        Member member = memberRepository.getOrThrow(memberId);
        List<Long> reservationIdList = findReservationIdListByMemberId(member);

        List<Ticket> tickets = ticketRepository.findByReservationIdIn(reservationIdList);

        return tickets.stream()
                .map(TicketResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse findTicket(Long ticketId) {
        Ticket ticket = findTicketById(ticketId);
        return TicketResponse.from(ticket);
    }

    private List<Long> findReservationIdListByMemberId(Member member) {
        return reservationRepository.findByMemberId(member.getId())
                .stream()
                .map(Reservation::getId)
                .toList();
    }

    private Ticket findTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket Not Found"));
    }
}
