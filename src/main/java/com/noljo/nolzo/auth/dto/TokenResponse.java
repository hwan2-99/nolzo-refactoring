package com.noljo.nolzo.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
