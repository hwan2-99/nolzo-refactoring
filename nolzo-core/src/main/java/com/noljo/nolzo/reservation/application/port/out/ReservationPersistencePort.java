package com.noljo.nolzo.reservation.application.port.out;

import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationPersistencePort {

    Optional<Reservation> findById(Long id);

    <S extends Reservation> S save(S reservation);

    <S extends Reservation> S saveAndFlush(S reservation);

    void delete(Reservation reservation);

    long count();

    List<Reservation> findAll();

    List<Reservation> findByMemberId(Long memberId);

    default Reservation getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found"));
    }

    List<Reservation> findReservationsByMemberId(Long memberId);

    List<Reservation> findReservationsStatusConfirmedByMemberId(Long memberId);

    List<Reservation> findTicketStatusUsedByMemberId(Long memberId);

    List<Reservation> findCanceledReservationsFetchAll(Long memberId);

    Reservation findReservationDetailsByMemberId(Long memberId, Long reservationId);

    List<Reservation> findByStatusAndCreatedAtBefore(ReservationStatus status, LocalDateTime time);

    Optional<Reservation> findByIdempotencyKey(String idempotencyKey);
}
