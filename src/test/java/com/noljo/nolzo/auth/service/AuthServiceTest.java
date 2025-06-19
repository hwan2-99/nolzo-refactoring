package com.noljo.nolzo.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.noljo.nolzo.auth.dto.RegisterRequest;
import com.noljo.nolzo.auth.dto.RegisterResponse;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.MemberFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@ServiceTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void 회원가입시_비밀번호가_암호화되서_회원이_저장된다() {
        Member member = MemberFixture.회원();
        RegisterRequest request = new RegisterRequest(member.getEmail(), member.getPassword(), member.getName(),
                member.getBirth());
        RegisterResponse response = authService.register(request);

        Member savedMember = memberRepository.findByEmail(request.email()).get();
        assertThat(savedMember.getId()).isEqualTo(response.memberId());
        assertThat(passwordEncoder.matches(request.password(), savedMember.getPassword())).isTrue();
    }
}