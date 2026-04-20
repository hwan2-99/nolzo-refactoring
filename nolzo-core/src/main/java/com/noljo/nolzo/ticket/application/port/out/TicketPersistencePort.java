package com.noljo.nolzo.ticket.application.port.out;

import com.noljo.nolzo.ticket.entity.Ticket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TicketPersistencePort {

    Optional<Ticket> findById(Long id);

    <S extends Ticket> S save(S ticket);

    List<Ticket> findAll();

    List<Ticket> findByReservationIdIn(List<Long> reservationIdList);

    void updateExpiredTickets(LocalDate targetDate, LocalTime targetTime);
}
