package com.noljo.nolzo.ticket.adapter.out.persistence;

import com.noljo.nolzo.ticket.application.port.out.TicketPersistencePort;
import com.noljo.nolzo.ticket.entity.Ticket;
import com.noljo.nolzo.ticket.repository.TicketRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketPersistenceAdapter implements TicketPersistencePort {

    private final TicketRepository ticketRepository;

    @Override
    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public <S extends Ticket> S save(S ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> findByReservationIdIn(List<Long> reservationIdList) {
        return ticketRepository.findByReservationIdIn(reservationIdList);
    }

    @Override
    public void updateExpiredTickets(LocalDate targetDate, LocalTime targetTime) {
        ticketRepository.updateExpiredTickets(targetDate, targetTime);
    }
}
