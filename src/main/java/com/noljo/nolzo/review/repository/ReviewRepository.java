package com.noljo.nolzo.review.repository;

import com.noljo.nolzo.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    default Review getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
    }
    Optional<Object> findByMemberIdAndEventId(Long memberId, Long eventId);
}
