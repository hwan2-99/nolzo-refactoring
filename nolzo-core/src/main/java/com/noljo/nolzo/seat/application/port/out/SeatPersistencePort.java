package com.noljo.nolzo.seat.application.port.out;

import com.noljo.nolzo.seat.entity.Seat;
import java.util.List;
import java.util.Optional;

public interface SeatPersistencePort {

    <S extends Seat> S save(S seat);

    <S extends Seat> List<S> saveAll(Iterable<S> seats);

    List<Seat> findAll();

    List<Seat> findAllById(Iterable<Long> ids);

    Optional<Seat> findById(Long id);

    Optional<Seat> findByIdWithPessimisticLock(Long id);

    default Seat getOrThrow(Long id) {
        return findById(id).orElseThrow(
                () -> new IllegalArgumentException("Seat with id " + id + " not found"));
    }
}
