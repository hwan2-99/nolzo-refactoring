package com.noljo.nolzo.reservation.repository;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.reservation.dto.ReservationEventInfo;
import com.noljo.nolzo.reservation.entity.Reservation;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMemberId(Long memberId);

    default Reservation getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found")); // 에러코드 추후 통일화 필요
    }

    @Query("""
    SELECT DISTINCT r FROM Reservation r
    JOIN FETCH r.tickets t
    JOIN FETCH t.seat s
    JOIN FETCH s.event e
    JOIN FETCH e.schedules sch
    WHERE r.member.id = :memberId
""")
    List<Reservation> findReservationsByMemberId(@Param("memberId") Long memberId);

    @Query("""
    SELECT DISTINCT r FROM Reservation r
    JOIN FETCH r.tickets t
    JOIN FETCH t.seat s
    JOIN FETCH s.event e
    JOIN FETCH e.schedules sch
    WHERE r.member.id = :memberId
    AND r.status = 'CONFIRMED'
""")
    List<Reservation> findReservationsStatusConfirmedByMemberId(@Param("memberId") Long memberId);

    @Query("""
    SELECT DISTINCT r FROM Reservation r
    JOIN FETCH r.tickets t
    JOIN FETCH t.seat s
    JOIN FETCH s.event e
    JOIN FETCH e.schedules sch
    WHERE r.member.id = :memberId
    AND t.status = 'USED'
""")
    List<Reservation> findTicketStatusUsedByMemberId(@Param("memberId") Long memberId);

    @Query("""
    SELECT DISTINCT r FROM Reservation r
    JOIN FETCH r.tickets t
    JOIN FETCH t.seat s
    JOIN FETCH s.event e
    JOIN FETCH e.schedules sch
    WHERE r.member.id = :memberId
      AND (r.status = 'CANCELED' OR t.status = 'CANCELED')
""")
    List<Reservation> findCanceledReservationsFetchAll(@Param("memberId") Long memberId);

    @Query("""
    SELECT DISTINCT r FROM Reservation r
    JOIN FETCH r.tickets t
    JOIN FETCH t.seat s
    JOIN FETCH s.event e
    JOIN FETCH e.schedules sch
    WHERE r.member.id = :memberId
""")
    Reservation findReservationDetailsByMemberId(@Param("memberId") Long memberId);
}
