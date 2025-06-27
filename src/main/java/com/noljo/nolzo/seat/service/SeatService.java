package com.noljo.nolzo.seat.service;

import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.schedule.repository.ScheduleRepository;
import com.noljo.nolzo.seat.dto.SeatResponse;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;
import com.noljo.nolzo.seat.entity.SectionPrice;
import com.noljo.nolzo.seat.repository.SeatRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class SeatService {
    public static int SECTION_START_NUMBER = 1;
    public static int SECTION_END_NUMBER = 5;
    public static int SEAT_START_NUMBER = 1;
    public static int SEAT_END_NUMBER = 20;
    public static char ROW_START_NAME = 'A';
    public static char ROW_END_NAME = 'J';

    private final SeatRepository seatRepository;
    private final ScheduleRepository scheduleRepository;

    /*todo 공연과 공연 스캐쥴 등록시 해당 스케쥴에 대한 좌석들을 한번에 자동으로 만드는 메서드입니다.
           추후 공연장마저 관리할거면 수정해야할 메서드 입니다.
           성능상 문제가 상당히 많을 코드여서 추후 리팩토링 필수일 것 같습니다.
     */
    public List<SeatResponse> createSeats(Long scheduleId) {
        Schedule schedule = findScheduleById(scheduleId);
        List<Seat> seats = new ArrayList<>();

        for (int section = SECTION_START_NUMBER; section <= SECTION_END_NUMBER; section++) {
            for (char row = ROW_START_NAME; row <= ROW_END_NAME; row++) {
                for (int seatNumber = SEAT_START_NUMBER; seatNumber <= SEAT_END_NUMBER; seatNumber++) {
                    Seat seat = new Seat(String.valueOf(row), seatNumber, section + "구역", "1층",
                            SectionPrice.getPriceBySection(section),
                            SeatStatus.AVAILABLE, schedule);
                    seats.add(seat);
                }
            }
        }

        seatRepository.saveAll(seats);

        return seats.stream()
                .map(SeatResponse::from)
                .toList();
    }

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

    private Schedule findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("No schedule found for the given id"));
    }
}
