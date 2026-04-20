package com.noljo.nolzo.ticket.repository;

import com.noljo.nolzo.ticket.entity.Ticket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByReservationIdIn(List<Long> reservationIdList);

    @Modifying
    @Query("""
            UPDATE Ticket t
            SET t.status = 'USED'
            WHERE t.status != 'USED'
            AND t.seat.schedule.showDate = :targetDate
            AND t.seat.schedule.showTime <= :targetTime
            """)
    void updateExpiredTickets(@Param("targetDate") LocalDate targetDate,
                              @Param("targetTime") LocalTime targetTime);
}
