package com.noljo.nolzo.ticket.application.port.in;

import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.ticket.dto.TicketResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TicketUseCase {

    TicketResponse create(Reservation reservation, Long seatId);

    List<TicketResponse> findTickets(Long memberId);

    TicketResponse findTicket(Long ticketId);

    void updateTicketStatusUsed(LocalDate targetDate, LocalTime targetTime);
}
