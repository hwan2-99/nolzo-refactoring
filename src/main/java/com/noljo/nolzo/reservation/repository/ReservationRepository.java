package com.noljo.nolzo.reservation.repository;

import com.noljo.nolzo.reservation.entity.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMemberId(Long memberId);

    default Reservation getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("not found")); // 에러코드 추후 통일화 필요
    }
}
