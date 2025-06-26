package com.noljo.nolzo.schedule.repository;

import com.noljo.nolzo.schedule.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
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
}
