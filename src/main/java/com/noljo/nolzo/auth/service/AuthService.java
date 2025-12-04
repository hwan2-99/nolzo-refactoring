package com.noljo.nolzo.auth.service;

import static javax.crypto.Cipher.SECRET_KEY;

import com.noljo.nolzo.auth.dto.AccessTokenResponse;
import com.noljo.nolzo.auth.dto.LoginRequest;
import com.noljo.nolzo.auth.dto.RegisterRequest;
import com.noljo.nolzo.auth.dto.RegisterResponse;
import com.noljo.nolzo.auth.dto.TokensResponse;
import com.noljo.nolzo.auth.entity.RefreshToken;
import com.noljo.nolzo.auth.jwt.JwtUtil;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.entity.Role;
import com.noljo.nolzo.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        validateDuplicateEmail(request.email());

        Member member = Member.of(
                request.name(),
                request.email(),
                request.password(),
                request.birth(),
                Role.USER
        );

        Member savedMember = memberRepository.save(member);
        return RegisterResponse.from(savedMember);
    }

    // todo: 로그인 실패시 에러 메시지 처리 구체화
    @Transactional
    public TokensResponse login(LoginRequest request, String clientIp) {
        Member member = findMemberByEmail(request.email(), request.password());
        RefreshToken refreshToken = jwtTokenService.findRefreshTokenByMember(member.getId());
        if (refreshToken != null && !isSameIp(refreshToken, clientIp)) {
            logout(refreshToken.getRefreshToken());
        }
        return jwtTokenService.issueToken(member, clientIp);
    }

    @Transactional
    public void logout(String refreshToken) {
        jwtTokenService.removeRefreshTokenByToken(refreshToken);
    }

    @Transactional
    public AccessTokenResponse reissueAccessToken(String refreshToken) {
        Long memberId = jwtUtil.getMemberId(refreshToken);
        Member member = memberRepository.getOrThrow(memberId);
        String reissuedAccessToken = jwtTokenService.reissueAccessToken(member, refreshToken);
        return new AccessTokenResponse(reissuedAccessToken);
    }

    private Member findMemberByEmail(String email, String password) {
        return memberRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 입니다."));
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0];
    }

    private boolean isSameIp(RefreshToken refreshToken, String clientIp) {
        return refreshToken.getRefreshToken().equals(clientIp);
    }
}
