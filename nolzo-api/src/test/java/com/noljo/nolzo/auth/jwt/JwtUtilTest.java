package com.noljo.nolzo.auth.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
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
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MemberPersistencePort memberPersistencePort;

    @Value("${jwt.secret}")
    private String secret;

    @Test
    void JWT_토큰_발행() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);

        String token = jwtUtil.createAccessToken(member);
        assertThat(jwtUtil.isExpired(token)).isFalse();
        assertThat(jwtUtil.getMemberId(token)).isEqualTo(member.getId());
        assertThat(jwtUtil.getEmail(token)).isEqualTo(member.getEmail());
        assertThat(jwtUtil.getRole(token)).isEqualTo(member.getRole());
    }

    @Test
    void JWT_토큰_만료시_검증이_실패한다() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);

        SecretKeySpec secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                SIG.HS256.key().build().getAlgorithm()
        );

        Date now = new Date();
        String expiredToken = Jwts.builder()
                .subject(member.getId().toString())
                .claim("email", member.getEmail())
                .claim("role", member.getRole().name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() - 1))
                .signWith(secretKey)
                .compact();

        assertThat(jwtUtil.isTokenValid(expiredToken)).isFalse();
    }

    @Test
    void JWT_토큰의_서명이_일치하지_않으면_검증이_실패한다() {
        Member member = MemberFixture.회원();
        memberPersistencePort.save(member);

        SecretKeySpec wrongKey = new SecretKeySpec(
                "some-wrong-secret-key-some-wrong-secret-key-some-wrong-secret-key".getBytes(StandardCharsets.UTF_8),
                SIG.HS256.key().build().getAlgorithm()
        );

        Date now = new Date();
        String invalidSignatureToken = Jwts.builder()
                .subject(member.getId().toString())
                .claim("email", member.getEmail())
                .claim("role", member.getRole().name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 60_000L))
                .signWith(wrongKey)
                .compact();

        assertThat(jwtUtil.isTokenValid(invalidSignatureToken)).isFalse();
    }
}
