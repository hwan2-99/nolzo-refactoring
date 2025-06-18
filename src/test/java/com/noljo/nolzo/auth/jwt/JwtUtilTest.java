package com.noljo.nolzo.auth.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.support.fixture.MemberFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void JWT_토큰_발행() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);

        String token = jwtUtil.createAccessToken(member);
        assertThat(jwtUtil.isExpired(token)).isFalse();
        assertThat(jwtUtil.getMemberId(token)).isEqualTo(member.getId());
        assertThat(jwtUtil.getEmail(token)).isEqualTo(member.getEmail());
        assertThat(jwtUtil.getRole(token)).isEqualTo(member.getRole());
    }
}