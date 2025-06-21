package com.noljo.nolzo.auth.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.noljo.nolzo.auth.dto.TokenResponse;
import com.noljo.nolzo.auth.repository.RefreshTokenRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.MemberFixture;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@ServiceTest
class JwtTokenUtilTest {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Test
    void 토큰을_발행하면_refreshToken이_저장된다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);

        TokenResponse tokenResponse = jwtTokenUtil.issueToken(member);

        String refreshToken = tokenResponse.refreshToken();

        assertThat(refreshTokenRepository.findByMemberId(member.getId())).isEqualTo(refreshToken);
    }

    @Test
    void accessToken은_재발급이_가능하다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);
        TokenResponse tokens = jwtTokenUtil.issueToken(member);

        String newAccessToken = jwtTokenUtil.reissueAccessToken(member, tokens.refreshToken());

        assertThat(jwtUtil.isTokenValid(newAccessToken)).isTrue();
        assertThat(jwtUtil.getMemberId(newAccessToken)).isEqualTo(member.getId());
    }

    @Test
    void 만료된_refreshToken_으로_재발급_실패() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);

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

        refreshTokenRepository.save(member.getId(), expiredRefreshToken);

        assertThatThrownBy(() -> jwtTokenUtil.reissueAccessToken(member, expiredRefreshToken))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 일치하지_않는_refreshToken_으로_재발급_실패() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);

        TokenResponse tokens = jwtTokenUtil.issueToken(member);

        String fakeToken = tokens.refreshToken() + "fake-token";

        assertThatThrownBy(() -> jwtTokenUtil.reissueAccessToken(member, fakeToken))
                .isInstanceOf(IllegalStateException.class);
    }
}