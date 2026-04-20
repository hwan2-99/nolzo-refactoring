package com.noljo.nolzo.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewUpdateRequest(
        @NotBlank
        String content,

        @Min(1) @Max(5)
        int rating
) {
}
