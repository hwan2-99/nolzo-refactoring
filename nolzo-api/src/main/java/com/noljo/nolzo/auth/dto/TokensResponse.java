package com.noljo.nolzo.auth.dto;

public record TokensResponse(
        String accessToken,
        String refreshToken
) {
}
