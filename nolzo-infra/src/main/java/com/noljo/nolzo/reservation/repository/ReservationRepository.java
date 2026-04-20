package com.noljo.nolzo.reservation.repository;

import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMemberId(Long memberId);

    @Query("""
                SELECT DISTINCT r FROM Reservation r
                JOIN FETCH r.tickets t
                JOIN FETCH t.seat s
                JOIN FETCH s.schedule sch
                JOIN FETCH sch.event e
                WHERE r.member.id = :memberId
            """)
    java.util.List<Reservation> findReservationsByMemberId(@Param("memberId") Long memberId);

    @Query("""
                SELECT DISTINCT r FROM Reservation r
                JOIN FETCH r.tickets t
                JOIN FETCH t.seat s
                JOIN FETCH s.schedule sch
                JOIN FETCH sch.event e
                WHERE r.member.id = :memberId
                AND r.status = 'CONFIRMED'
            """)
    java.util.List<Reservation> findReservationsStatusConfirmedByMemberId(@Param("memberId") Long memberId);

    @Query("""
                SELECT DISTINCT r FROM Reservation r
                JOIN FETCH r.tickets t
                JOIN FETCH t.seat s
                JOIN FETCH s.schedule sch
                JOIN FETCH sch.event e
                WHERE r.member.id = :memberId
                AND t.status = 'USED'
            """)
    java.util.List<Reservation> findTicketStatusUsedByMemberId(@Param("memberId") Long memberId);

    @Query("""
                SELECT DISTINCT r FROM Reservation r
                JOIN FETCH r.tickets t
                JOIN FETCH t.seat s
                JOIN FETCH s.schedule sch
                JOIN FETCH sch.event e
                WHERE r.member.id = :memberId
                  AND (r.status = 'CANCELED' OR t.status = 'CANCELED')
            """)
    java.util.List<Reservation> findCanceledReservationsFetchAll(@Param("memberId") Long memberId);

    @Query("""
                SELECT DISTINCT r FROM Reservation r
                JOIN FETCH r.tickets t
                JOIN FETCH t.seat s
                JOIN FETCH s.schedule sch
                JOIN FETCH sch.event e
                WHERE r.member.id = :memberId AND r.id = :reservationId
            """)
    Reservation findReservationDetailsByMemberId(@Param("memberId") Long memberId,
                                                 @Param("reservationId") Long reservationId);

    List<Reservation> findByStatusAndCreatedAtBefore(ReservationStatus status, LocalDateTime time);

    Optional<Reservation> findByIdempotencyKey(String idempotencyKey);
}
