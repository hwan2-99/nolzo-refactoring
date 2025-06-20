package com.noljo.nolzo.auth.repository;

public interface RefreshTokenRepository {
    void save(Long memberId, String refreshToken);

    String findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}