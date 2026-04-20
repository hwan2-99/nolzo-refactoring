package com.noljo.nolzo.seat.service;

import com.noljo.nolzo.global.aop.lock.DistributedLock;
import com.noljo.nolzo.global.error.exception.SeatException;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.schedule.application.port.out.SchedulePersistencePort;
import com.noljo.nolzo.seat.dto.SeatResponse;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;
import com.noljo.nolzo.seat.entity.SectionPrice;
import com.noljo.nolzo.seat.application.port.out.SeatPersistencePort;
import com.noljo.nolzo.ticket.entity.Ticket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {
    public static int SECTION_START_NUMBER = 1;
    public static int SECTION_END_NUMBER = 5;
    public static int SEAT_START_NUMBER = 1;
    public static int SEAT_END_NUMBER = 20;
    public static char ROW_START_NAME = 'A';
    public static char ROW_END_NAME = 'J';

    private final SeatPersistencePort seatRepository;
    private final SchedulePersistencePort scheduleRepository;

    /*todo 공연과 공연 스캐쥴 등록시 해당 스케쥴에 대한 좌석들을 한번에 자동으로 만드는 메서드입니다.
           추후 공연장마저 관리할거면 수정해야할 메서드 입니다.
           성능상 문제가 상당히 많을 코드여서 추후 리팩토링 필수일 것 같습니다.
     */
    @Transactional
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

    @Transactional
    public void updateWithReservation(List<Seat> seats) {
        for (Seat seat : seats) {
            Seat lockedSeat = findSeatByIdWithPessimisticLock(seat.getId());
            validateIsAvailable(lockedSeat);
            lockedSeat.updateStatus(SeatStatus.WAITING);
        }
    }

    @Transactional
    public void updateWithPayment(List<Ticket> tickets, SeatStatus seatStatus) {
        for (Ticket ticket : tickets) {
            Seat seat = ticket.getSeat();
            seat.updateStatus(seatStatus);
        }
    }

    private Seat findSeatByIdWithPessimisticLock(Long id) {
        return seatRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new IllegalArgumentException("Seat with id " + id + " not found"));
    }

    private void validateIsAvailable(Seat seat) {
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatException(seat.getId());
        }
    }

    @DistributedLock(key = "'seat:' + #seatId")
    public void updateWithRedisson(List<Long> seatIds) {
        for (Long seatId : seatIds) {
            Seat seat = seatRepository.getOrThrow(seatId);
            validateIsAvailable(seat);

            seat.updateStatus(SeatStatus.WAITING);
            log.info("Seat {} status updated to WAITING", seatId);
        }
    }

    @Transactional(readOnly = true)
    public int calculateTotalPrice(List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);

        return seats.stream()
                .mapToInt(Seat::getPrice)
                .sum();
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> findSeats(Long eventId, String date, String time) {
        return scheduleRepository.findSeatResponsesBySchedule(
                eventId, LocalDate.parse(date), LocalTime.parse(time));
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
