package com.noljo.nolzo.seat.service;

import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.schedule.repository.ScheduleRepository;
import com.noljo.nolzo.seat.dto.SeatResponse;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;
import com.noljo.nolzo.seat.repository.SeatRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    private final ScheduleRepository scheduleRepository;

    public void updateWithReservation(List<Seat> seats) {
        seats.forEach(seat -> {
            validateIsAvailable(seat);
            Seat lockedSeat = findSeatByIdWithPessimisticLock(seat.getId());
            lockedSeat.updateStatus(SeatStatus.WAITING);
        });
    }

    private Seat findSeatByIdWithPessimisticLock(Long id) {
        return seatRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new IllegalArgumentException("Seat with id " + id + " not found"));
    }

    private void validateIsAvailable(Seat seat) {
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new IllegalArgumentException("Seat with id " + seat.getId() + " is not available");
        }
    }
    @Transactional(readOnly = true)
    public List<SeatResponse> findSeats(Long eventId, String date, String time) {
        Schedule schedule = findScheduleByEventIdWithDate(eventId, date, time);

        return schedule.getSeats().stream()
                .map(SeatResponse::from)
                .toList();
    }

    private Schedule findScheduleByEventIdWithDate(Long eventId, String date, String time) {
        return scheduleRepository
                .findByEventIdAndShowDateAndShowTime(eventId, LocalDate.parse(date), LocalTime.parse(time))
                .orElseThrow(() -> new IllegalArgumentException("No schedule found for the given date and time"));
    }
}
