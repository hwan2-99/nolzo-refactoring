package com.noljo.nolzo.schedule.repository;

import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.seat.dto.SeatResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("""
            SELECT s FROM Schedule s
            WHERE s.event.id = :eventId
            AND s.showDate = :showDate
            AND s.showTime = :showTime
            """)
    Optional<Schedule> findByEventIdAndShowDateAndShowTime(
            @Param("eventId") Long eventId,
            @Param("showDate") LocalDate showDate,
            @Param("showTime") LocalTime showTime
    );

    @Query("""
            SELECT new com.noljo.nolzo.seat.dto.SeatResponse(se.id, se.rowName, se.seatNumber, se.seatSection, se.floor, se.price, se.status)
            FROM Schedule s
            JOIN s.seats se
            WHERE s.event.id = :eventId
            AND s.showDate = :showDate
            AND s.showTime = :showTime
            """)
    List<SeatResponse> findSeatResponsesBySchedule(
            @Param("eventId") Long eventId,
            @Param("showDate") LocalDate showDate,
            @Param("showTime") LocalTime showTime
    );
}
