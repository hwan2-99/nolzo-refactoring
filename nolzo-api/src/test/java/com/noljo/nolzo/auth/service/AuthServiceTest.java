package com.noljo.nolzo.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.noljo.nolzo.auth.dto.AccessTokenResponse;
import com.noljo.nolzo.auth.dto.LoginRequest;
import com.noljo.nolzo.auth.dto.RegisterRequest;
import com.noljo.nolzo.auth.dto.RegisterResponse;
import com.noljo.nolzo.auth.dto.TokensResponse;
import com.noljo.nolzo.auth.jwt.JwtUtil;
import com.noljo.nolzo.auth.repository.RefreshTokenRepository;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.MemberFixture;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@ServiceTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void 중복된_이메일로_회원가입시_예외가_발생한다() {
        Member member = MemberFixture.회원();
        RegisterRequest request = new RegisterRequest(member.getEmail(), member.getPassword(), member.getName(),
                member.getBirth());

        authService.register(request);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Transactional
    void 로그인시_토큰이_정상적으로_발급된다() {
        Member member = MemberFixture.회원();
        TokensResponse tokens = registerAndLogin(member);

        assertThat(jwtUtil.isTokenValid(tokens.accessToken())).isTrue();
        assertThat(jwtUtil.isTokenValid(tokens.refreshToken())).isTrue();
        assertThat(refreshTokenRepository.findAll()).hasSize(1);
    }

    @Test
    void 존재하지_않는_이메일로_로그인하면_예외가_발생한다() {
        LoginRequest login = new LoginRequest("notfound@example.com", "1234");

        assertThatThrownBy(() -> authService.login(login, "testIp"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 잘못된_비밀번호로_로그인하면_예외가_발생한다() {
        Member member = MemberFixture.회원();
        registerAndLogin(member);
        LoginRequest login = new LoginRequest(member.getEmail(), "wrong");

        assertThatThrownBy(() -> authService.login(login, "testIp"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 로그아웃시_refreshToken이_제거된다() {
        Member member = MemberFixture.회원();
        TokensResponse tokens = registerAndLogin(member);

        authService.logout(tokens.refreshToken());
        assertThat(refreshTokenRepository.findByToken(tokens.refreshToken())).isNull();
    }

    @Test
    void 유효한_refreshToken으로_accessToken을_재발급받는다() {
        Member member = MemberFixture.회원();
        TokensResponse tokens = registerAndLogin(member);

        AccessTokenResponse response = authService.reissueAccessToken(tokens.refreshToken());

        assertThat(jwtUtil.isTokenValid(response.accessToken())).isTrue();
    }

    @Test
    void 잘못된_refreshToken으로_재발급시_예외가_발생한다() {
        Member member = MemberFixture.회원();
        TokensResponse tokens = registerAndLogin(member);

        assertThatThrownBy(() -> authService.reissueAccessToken(tokens.refreshToken() + "wrong-token"))
                .isInstanceOf(SignatureException.class);
    }

    private TokensResponse registerAndLogin(Member member) {
        RegisterRequest registerRequest = new RegisterRequest(
                member.getEmail(), member.getPassword(), member.getName(), member.getBirth()
        );
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(member.getEmail(), member.getPassword());
        return authService.login(loginRequest, "testIp");
    }
}
