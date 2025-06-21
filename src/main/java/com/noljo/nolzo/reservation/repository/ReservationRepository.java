package com.noljo.nolzo.reservation.repository;

import com.noljo.nolzo.reservation.entity.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMemberId(Long memberId);

    default Reservation getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("not found")); // 에러코드 추후 통일화 필요
    }

    List<Reservation> findReservationsByMemberId(Long memberId);

    @Query("SELECT r FROM Reservation r JOIN r.tickets t WHERE r.member.id = :memberId AND t.status = 'CANCELED'")
    List<Reservation> findTicketStatusCanceledByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT r FROM Reservation r WHERE r.member.id = :memberId AND r.status = 'CANCELED'")
    List<Reservation> findReservationsStatusCanceledByMemberId(Long memberId);
}
