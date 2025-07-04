package com.noljo.nolzo.review.dto.response;

import com.noljo.nolzo.review.entity.Review;
import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        String content,
        int rating,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getContent(),
                review.getRating(),
                review.getCreatedAt()
        );
    }
}
