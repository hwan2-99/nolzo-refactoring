package com.noljo.nolzo.review.application.port.out;

import com.noljo.nolzo.review.entity.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewPersistencePort {

    Optional<Review> findById(Long id);

    <S extends Review> S save(S review);

    void delete(Review review);

    List<Review> findAll();

    default Review getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
    }

    List<Review> findByMemberId(Long memberId);

    Optional<Review> findByMemberIdAndEventId(Long memberId, Long eventId);

    Page<Review> findPageByEventId(Long eventId, Pageable pageable);

    Double getAverageByEventId(Long eventId);
}
