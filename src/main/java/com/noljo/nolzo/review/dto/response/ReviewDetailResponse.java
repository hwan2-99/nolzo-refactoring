package com.noljo.nolzo.review.dto.response;

import com.noljo.nolzo.review.entity.Review;
import java.time.LocalDateTime;

public record ReviewDetailResponse(
        Long id,
        String content,
        int rating,
        String author,
        LocalDateTime createdAt
) {
    public static ReviewDetailResponse from(Review review) {
        return new ReviewDetailResponse(
                review.getId(),
                review.getContent(),
                review.getRating(),
                review.getMember().getName(),
                review.getCreatedAt()
        );
    }
}
