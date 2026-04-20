package com.noljo.nolzo.ticket.service;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.application.port.out.ReservationPersistencePort;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.application.port.out.SeatPersistencePort;
import com.noljo.nolzo.ticket.dto.TicketResponse;
import com.noljo.nolzo.ticket.entity.Ticket;
import com.noljo.nolzo.ticket.entity.TicketStatus;
import com.noljo.nolzo.ticket.application.port.out.TicketPersistencePort;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class TicketService {
    private final MemberPersistencePort memberRepository;
    private final ReservationPersistencePort reservationRepository;
    private final TicketPersistencePort ticketRepository;
    private final SeatPersistencePort seatRepository;

    public TicketResponse create(Reservation reservation, Long seatId) {
        Seat seat = seatRepository.getOrThrow(seatId);
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

    public void updateTicketStatusUsed(LocalDate targetDate, LocalTime targetTime) {
        ticketRepository.updateExpiredTickets(targetDate, targetTime);
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
