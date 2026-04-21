package com.noljo.nolzo.ticket.service;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.application.port.out.ReservationPersistencePort;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.application.port.out.SeatPersistencePort;
import com.noljo.nolzo.ticket.application.port.in.TicketUseCase;
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
public class TicketService implements TicketUseCase {
    private final MemberPersistencePort memberPersistencePort;
    private final ReservationPersistencePort reservationPersistencePort;
    private final TicketPersistencePort ticketPersistencePort;
    private final SeatPersistencePort seatPersistencePort;

    public TicketResponse create(Reservation reservation, Long seatId) {
        Seat seat = seatPersistencePort.getOrThrow(seatId);
        Ticket ticket = new Ticket(TicketStatus.NOT_USED, reservation, seat);
        ticketPersistencePort.save(ticket);
        return TicketResponse.from(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> findTickets(Long memberId) {
        Member member = memberPersistencePort.getOrThrow(memberId);
        List<Long> reservationIdList = findReservationIdListByMemberId(member);

        List<Ticket> tickets = ticketPersistencePort.findByReservationIdIn(reservationIdList);

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
        ticketPersistencePort.updateExpiredTickets(targetDate, targetTime);
    }

    private List<Long> findReservationIdListByMemberId(Member member) {
        return reservationPersistencePort.findByMemberId(member.getId())
                .stream()
                .map(Reservation::getId)
                .toList();
    }

    private Ticket findTicketById(Long ticketId) {
        return ticketPersistencePort.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket Not Found"));
    }

}
