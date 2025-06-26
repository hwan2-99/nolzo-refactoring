package com.noljo.nolzo.seat.service;

import com.noljo.nolzo.seat.dto.SeatResponse;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;
import com.noljo.nolzo.seat.repository.SeatRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

//    public List<SeatResponse> createSeats(Long scheduleId) {
//
//    }

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

//    @Transactional(readOnly = true)
//    public List<SeatResponse> findSeatsByEventAndSchedule(Long eventId) {
//
//    }
}
