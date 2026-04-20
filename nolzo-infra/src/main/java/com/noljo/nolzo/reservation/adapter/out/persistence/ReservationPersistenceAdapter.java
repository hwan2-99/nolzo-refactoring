package com.noljo.nolzo.reservation.adapter.out.persistence;

import com.noljo.nolzo.reservation.application.port.out.ReservationPersistencePort;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationPersistenceAdapter implements ReservationPersistencePort {

    private final ReservationRepository reservationRepository;

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public <S extends Reservation> S save(S reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public <S extends Reservation> S saveAndFlush(S reservation) {
        return reservationRepository.saveAndFlush(reservation);
    }

    @Override
    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    @Override
    public long count() {
        return reservationRepository.count();
    }

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public List<Reservation> findByMemberId(Long memberId) {
        return reservationRepository.findByMemberId(memberId);
    }

    @Override
    public List<Reservation> findReservationsByMemberId(Long memberId) {
        return reservationRepository.findReservationsByMemberId(memberId);
    }

    @Override
    public List<Reservation> findReservationsStatusConfirmedByMemberId(Long memberId) {
        return reservationRepository.findReservationsStatusConfirmedByMemberId(memberId);
    }

    @Override
    public List<Reservation> findTicketStatusUsedByMemberId(Long memberId) {
        return reservationRepository.findTicketStatusUsedByMemberId(memberId);
    }

    @Override
    public List<Reservation> findCanceledReservationsFetchAll(Long memberId) {
        return reservationRepository.findCanceledReservationsFetchAll(memberId);
    }

    @Override
    public Reservation findReservationDetailsByMemberId(Long memberId, Long reservationId) {
        return reservationRepository.findReservationDetailsByMemberId(memberId, reservationId);
    }

    @Override
    public List<Reservation> findByStatusAndCreatedAtBefore(ReservationStatus status, LocalDateTime time) {
        return reservationRepository.findByStatusAndCreatedAtBefore(status, time);
    }

    @Override
    public Optional<Reservation> findByIdempotencyKey(String idempotencyKey) {
        return reservationRepository.findByIdempotencyKey(idempotencyKey);
    }
}
