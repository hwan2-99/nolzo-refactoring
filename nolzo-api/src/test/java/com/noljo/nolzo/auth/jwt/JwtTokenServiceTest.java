package com.noljo.nolzo.auth.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.noljo.nolzo.auth.dto.TokensResponse;
import com.noljo.nolzo.auth.entity.RefreshToken;
import com.noljo.nolzo.auth.application.port.out.RefreshTokenPersistencePort;
import com.noljo.nolzo.auth.scheduler.RefreshTokenCleanupScheduler;
import com.noljo.nolzo.auth.service.JwtTokenService;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.MemberFixture;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@ServiceTest
class JwtTokenServiceTest {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenPersistencePort refreshTokenPersistencePort;

    @Autowired
    private MemberPersistencePort memberPersistencePort;

    @Autowired
    private RefreshTokenCleanupScheduler refreshTokenCleanupScheduler;

    @Value("${jwt.secret}")
    private String secret;

    @Test
    void 토큰을_발행하면_refreshToken이_저장된다() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);

        TokensResponse tokensResponse = jwtTokenService.issueToken(member, "test");
        assertThat(refreshTokenPersistencePort.findByMemberId(member.getId()).get().getRefreshToken()).isEqualTo(
                tokensResponse.refreshToken());
    }

    @Test
    void accessToken은_재발급이_가능하다() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);
        TokensResponse tokens = jwtTokenService.issueToken(member, "test");

        String newAccessToken = jwtTokenService.reissueAccessToken(member, tokens.refreshToken());

        assertThat(jwtUtil.isTokenValid(newAccessToken)).isTrue();
        assertThat(jwtUtil.getMemberId(newAccessToken)).isEqualTo(member.getId());
    }

    @Test
    void 만료된_refreshToken_으로_재발급_실패() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);

        String expiredRefreshToken = Jwts.builder()
                .subject(member.getId().toString())
                .claim("email", member.getEmail())
                .claim("role", member.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() - 1000)) // 만료됨
                .signWith(new SecretKeySpec(
                        secret.getBytes(StandardCharsets.UTF_8),
                        SIG.HS256.key().build().getAlgorithm()
                ))
                .compact();

        refreshTokenPersistencePort.save(
                new RefreshToken(member.getId(), expiredRefreshToken, LocalDateTime.now().minusSeconds(1)));

        assertThatThrownBy(() -> jwtTokenService.reissueAccessToken(member, expiredRefreshToken))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 일치하지_않는_refreshToken_으로_재발급_실패() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);

        TokensResponse tokens = jwtTokenService.issueToken(member, "test");

        String fakeToken = tokens.refreshToken() + "fake-token";

        assertThatThrownBy(() -> jwtTokenService.reissueAccessToken(member, fakeToken))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void refreshToken으로_토큰_삭제_성공() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);
        TokensResponse tokens = jwtTokenService.issueToken(member, "test");
        String refreshToken = tokens.refreshToken();

        jwtTokenService.removeRefreshTokenByToken(refreshToken);

        assertThat(refreshTokenPersistencePort.findByMemberId(member.getId())).isEmpty();
    }

    @Test
    void 만료된_refreshToken들이_제거된다() {
        Member member1 = MemberFixture.회원();
        Member member2 = MemberFixture.회투();
        memberPersistencePort.save(member1);
        memberPersistencePort.save(member2);

        LocalDateTime expiredDate = LocalDateTime.now().minusDays(1);
        RefreshToken expiredToken1 = new RefreshToken(member1.getId(), "expired-token-1", expiredDate);
        RefreshToken expiredToken2 = new RefreshToken(member2.getId(), "expired-token-2", expiredDate);

        LocalDateTime validDate = LocalDateTime.now().plusDays(1);
        RefreshToken validToken = new RefreshToken(member1.getId(), "valid-token", validDate);

        refreshTokenPersistencePort.saveAll(List.of(expiredToken1, expiredToken2, validToken));

        refreshTokenCleanupScheduler.cleanupExpiredRefreshTokens();

        List<RefreshToken> remainingTokens = refreshTokenPersistencePort.findAll();
        assertThat(remainingTokens).hasSize(1);
        assertThat(remainingTokens.get(0).getRefreshToken()).isEqualTo("valid-token");
    }
}
