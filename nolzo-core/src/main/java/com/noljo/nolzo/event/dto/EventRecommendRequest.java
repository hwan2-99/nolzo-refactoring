package com.noljo.nolzo.event.dto;

import jakarta.validation.constraints.NotBlank;

public record EventRecommendRequest(
        Long memberId,
        @NotBlank(message = "추천 질의는 비어 있을 수 없습니다.")
        String query
) {
}
