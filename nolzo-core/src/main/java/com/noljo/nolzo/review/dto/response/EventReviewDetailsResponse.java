package com.noljo.nolzo.review.dto.response;

import java.util.List;

public record EventReviewDetailsResponse(
        double averageRating,
        int reviewCount,
        List<ReviewDetailResponse> reviews
) {
}
