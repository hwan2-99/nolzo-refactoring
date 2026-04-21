package com.noljo.nolzo.seat.adapter.out.persistence;

import com.noljo.nolzo.seat.application.port.out.SeatPersistencePort;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.repository.SeatRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatPersistenceAdapter implements SeatPersistencePort {

    private final SeatRepository seatRepository;

    @Override
    public <S extends Seat> S save(S seat) {
        return seatRepository.save(seat);
    }

    @Override
    public <S extends Seat> List<S> saveAll(Iterable<S> seats) {
        return seatRepository.saveAll(seats);
    }

    @Override
    public List<Seat> findAll() {
        return seatRepository.findAll();
    }

    @Override
    public List<Seat> findAllById(Iterable<Long> ids) {
        return seatRepository.findAllById(ids);
    }

    @Override
    public Optional<Seat> findById(Long id) {
        return seatRepository.findById(id);
    }

    @Override
    public Optional<Seat> findByIdWithPessimisticLock(Long id) {
        return seatRepository.findByIdWithPessimisticLock(id);
    }
}
