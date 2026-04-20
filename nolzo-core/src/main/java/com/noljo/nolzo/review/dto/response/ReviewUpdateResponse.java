package com.noljo.nolzo.review.dto.response;

import com.noljo.nolzo.review.entity.Review;
import java.time.LocalDateTime;

public record ReviewUpdateResponse(
        String content,
        int rating,
        LocalDateTime modifiedAt

) {
    public static ReviewUpdateResponse from(Review review) {
        return new ReviewUpdateResponse(
                review.getContent(),
                review.getRating(),
                review.getModifiedAt()
        );
    }
}
