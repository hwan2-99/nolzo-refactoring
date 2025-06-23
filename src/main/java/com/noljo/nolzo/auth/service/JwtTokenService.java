package com.noljo.nolzo.auth.service;

import com.noljo.nolzo.auth.dto.TokensResponse;
import com.noljo.nolzo.auth.jwt.JwtUtil;
import com.noljo.nolzo.auth.repository.RefreshTokenRepository;
import com.noljo.nolzo.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokensResponse issueToken(Member member) {
        String accessToken = jwtUtil.createAccessToken(member);
        String refreshToken = jwtUtil.createRefreshToken(member);
        refreshTokenRepository.save(member.getId(), refreshToken);
        return new TokensResponse(accessToken, refreshToken);
    }

    public String reissueAccessToken(Member member, String refreshToken) {
        validateRefreshToken(refreshToken);
        validateRefreshTokenExists(member.getId());
        validateRefreshTokenNotExpired(member.getId(), refreshToken);
        validateRefreshTokenMatches(member.getId(), refreshToken);

        return jwtUtil.createAccessToken(member);
    }

    public void removeRefreshToken(Long memberId) {
        validateRefreshTokenExists(memberId);
        refreshTokenRepository.deleteByMemberId(memberId);
    }

    public boolean hasRefreshToken(Long memberId) {
        try {
            validateRefreshTokenExists(memberId);
        } catch (IllegalStateException e) {
            return false;
        }
        return true;
    }

    private void validateRefreshToken(String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new IllegalStateException("유효하지 않은 Token 입니다.");
        }
    }

    private void validateRefreshTokenExists(Long memberId) {
        if (refreshTokenRepository.findByMemberId(memberId) == null) {
            throw new IllegalStateException("저장된 RefreshToken이 없습니다.");
        }
    }

    private void validateRefreshTokenNotExpired(Long memberId, String refreshToken) {
        if (jwtUtil.isExpired(refreshToken)) {
            refreshTokenRepository.deleteByMemberId(memberId);
            throw new IllegalStateException("RefreshToken이 만료되었습니다.");
        }
    }

    private void validateRefreshTokenMatches(Long memberId, String refreshToken) {
        String saved = refreshTokenRepository.findByMemberId(memberId);
        if (!refreshToken.equals(saved)) {
            throw new IllegalStateException("RefreshToken이 일치하지 않습니다.");
        }
    }
}
