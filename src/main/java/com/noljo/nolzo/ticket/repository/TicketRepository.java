package com.noljo.nolzo.ticket.repository;

import com.noljo.nolzo.ticket.entity.Ticket;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByReservationIdIn(List<Long> reservationIdList);

    @Modifying
    @Query(value = """
            UPDATE ticket t
            JOIN seat s ON t.seat_id = s.seat_id
            JOIN schedule sc ON s.schedule_id = sc.schedule_id
            SET t.status = 'USED'
            WHERE t.status != 'USED'
            AND sc.show_date = :targetDate
            AND sc.show_time <= :targetTime
            """, nativeQuery = true)
    void updateExpiredTickets(@Param("targetDate") LocalDate targetDate,
                              @Param("targetTime") LocalTime targetTime);
}
