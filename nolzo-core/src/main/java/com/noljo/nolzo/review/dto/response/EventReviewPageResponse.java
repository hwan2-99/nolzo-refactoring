package com.noljo.nolzo.review.dto.response;

import com.noljo.nolzo.review.entity.Review;
import java.util.List;
import org.springframework.data.domain.Page;

public record EventReviewPageResponse(
        double averageRating,
        long totalReviewCount,
        List<ReviewDetailResponse> reviews,
        int currentPage,
        int totalPages,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious
) {
    public static EventReviewPageResponse of(
            Page<Review> reviewPage,
            List<ReviewDetailResponse> reviewResponses,
            double averageRating,
            int page,
            int size
    ) {
        return new EventReviewPageResponse(
                averageRating,
                reviewPage.getTotalElements(),
                reviewResponses,
                page,
                reviewPage.getTotalPages(),
                size,
                reviewPage.hasNext(),
                reviewPage.hasPrevious()
        );
    }
}
