package com.noljo.nolzo.review.repository;

import com.noljo.nolzo.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Object> findByMemberIdAndEventId(Long memberId, Long eventId);
}
