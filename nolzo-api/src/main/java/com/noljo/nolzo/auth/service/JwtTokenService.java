package com.noljo.nolzo.auth.service;

import com.noljo.nolzo.auth.dto.TokensResponse;
import com.noljo.nolzo.auth.entity.RefreshToken;
import com.noljo.nolzo.auth.jwt.JwtUtil;
import com.noljo.nolzo.auth.application.port.out.RefreshTokenPersistencePort;
import com.noljo.nolzo.member.entity.Member;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtTokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenPersistencePort refreshTokenRepository;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    public TokensResponse issueToken(Member member, String clientIp) {
        RefreshToken refreshToken = saveRefreshToken(member, jwtUtil.createRefreshToken(member), calculateExpiryDate(),
                clientIp);
        return new TokensResponse(jwtUtil.createAccessToken(member), refreshToken.getRefreshToken());
    }

    public String reissueAccessToken(Member member, String refreshToken) {
        validateRefreshToken(refreshToken);
        RefreshToken savedRefreshToken = getRefreshToken(member.getId());
        validateRefreshTokenNotExpired(savedRefreshToken);
        validateRefreshTokenMatches(savedRefreshToken, refreshToken);
        return jwtUtil.createAccessToken(member);
    }

    public void removeRefreshTokenByToken(String refreshToken) {
        RefreshToken findToken = refreshTokenRepository.findByToken(refreshToken);
        if (findToken != null) {
            refreshTokenRepository.deleteByMemberId(findToken.getMemberId());
        }
    }

    public RefreshToken findRefreshTokenByMember(Long memberId) {
        return refreshTokenRepository.findByMemberId(memberId)
                .orElse(null);
    }

    private RefreshToken saveRefreshToken(Member member, String refreshToken, LocalDateTime expiryDate,
                                          String clientIp) {
        return refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken, expiryDate, clientIp));
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusSeconds(refreshTokenValidityInSeconds);
    }

    private void validateRefreshToken(String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new IllegalStateException("유효하지 않은 Token 입니다.");
        }
    }

    private RefreshToken getRefreshToken(Long memberId) {
        return refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("RefreshToken을 찾을 수 없습니다."));
    }

    private void validateRefreshTokenNotExpired(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalStateException("RefreshToken이 만료되었습니다.");
        }
    }

    private void validateRefreshTokenMatches(RefreshToken savedRefreshToken, String refreshToken) {
        if (!refreshToken.equals(savedRefreshToken.getRefreshToken())) {
            throw new IllegalStateException("RefreshToken이 일치하지 않습니다.");
        }
    }
}
