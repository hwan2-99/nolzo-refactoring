package com.noljo.nolzo.seat.service;

import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.schedule.repository.ScheduleRepository;
import com.noljo.nolzo.seat.dto.SeatResponse;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;
import com.noljo.nolzo.seat.entity.SectionPrice;
import com.noljo.nolzo.seat.repository.SeatRepository;
import com.noljo.nolzo.ticket.entity.Ticket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    private final RedissonClient redissonClient;

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
        for (Seat seat : seats) {
            Seat lockedSeat = findSeatByIdWithPessimisticLock(seat.getId());
            validateIsAvailable(lockedSeat);
            lockedSeat.updateStatus(SeatStatus.WAITING);
        }
    }

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
            throw new IllegalArgumentException("Seat with id " + seat.getId() + " is already reserved.");
        }
    }

    @Transactional
    public void updateWithRedisson(List<Seat> seats) {
        for (Seat seat : seats) {
            selectSeat(seat.getId());
        }
    }

    private void selectSeat(Long seatId) {
        String lockKey = "seat-lock:" + seatId;
        RLock lock = redissonClient.getLock(lockKey);

        boolean locking = false;
        try {
            locking = lock.tryLock(1, 300, TimeUnit.SECONDS);
            if (!locking) {
                log.warn("Lock 획득 실패: seatId={}", seatId);
                throw new IllegalStateException("해당 좌석은 현재 선택 중입니다. 잠시 후 다시 시도해주세요.");
            }
            Seat seat = seatRepository.getOrThrow(seatId);

            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                log.info("선택 불가 좌석 상태: seatId={}, status={}", seatId, seat.getStatus());
                throw new IllegalArgumentException("해당 좌석은 선택할 수 없습니다.");
            }

            seat.updateStatus(SeatStatus.WAITING);

            seatRepository.save(seat);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 대기 중 인터럽트 발생", e);
        } finally {
            if (locking && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
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
