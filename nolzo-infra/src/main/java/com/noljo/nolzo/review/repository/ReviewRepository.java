package com.noljo.nolzo.review.repository;

import com.noljo.nolzo.review.entity.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMemberId(Long memberId);

    Optional<Review> findByMemberIdAndEventId(Long memberId, Long eventId);

    @Query(value = "SELECT r FROM Review r JOIN FETCH r.member WHERE r.event.id = :eventId",
            countQuery = "SELECT count(r) FROM Review r WHERE r.event.id = :eventId")
    Page<Review> findPageByEventId(@Param("eventId") Long eventId, Pageable pageable);

    @Query("SELECT IFNULL(AVG(r.rating), 0.0) FROM Review r WHERE r.event.id = :eventId")
    Double getAverageByEventId(@Param("eventId") Long eventId);
}
