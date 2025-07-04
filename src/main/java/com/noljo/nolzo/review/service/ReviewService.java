package com.noljo.nolzo.review.service;

import com.noljo.nolzo.review.dto.request.ReviewUpdateRequest;
import com.noljo.nolzo.review.dto.response.ReviewUpdateResponse;
import com.noljo.nolzo.review.entity.Review;
import com.noljo.nolzo.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewUpdateResponse update(Long memberId, Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.getOrThrow(reviewId);
        validateReviewOwner(review, memberId);
        review.update(request.content(), request.rating());

        return ReviewUpdateResponse.from(review);
    }

    private void validateReviewOwner(Review review, Long memberId) {
        if (!review.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("해당 리뷰를 수정할 권한이 없습니다.");
        }
    }
}
