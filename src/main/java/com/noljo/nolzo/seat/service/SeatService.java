package com.noljo.nolzo.seat.service;

import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;
import com.noljo.nolzo.seat.repository.SeatRepository;
import jakarta.persistence.LockModeType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

@Transactional
@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

    public void updateWithReservation(List<Seat> seats) {
        seats.forEach(seat -> {
            Seat lockedSeat = findSeatByIdWithPessimisticLock(seat.getId());
            lockedSeat.updateStatus(SeatStatus.WAITING);
            seatRepository.save(lockedSeat);
        });
    }

    private Seat findSeatByIdWithPessimisticLock(Long id) {
        return seatRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new NotFoundException("Seat with id " + id + " not found"));
    }
}
