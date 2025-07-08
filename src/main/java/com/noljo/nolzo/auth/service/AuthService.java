package com.noljo.nolzo.auth.service;

import com.noljo.nolzo.auth.dto.AccessTokenResponse;
import com.noljo.nolzo.auth.dto.LoginRequest;
import com.noljo.nolzo.auth.dto.RegisterRequest;
import com.noljo.nolzo.auth.dto.RegisterResponse;
import com.noljo.nolzo.auth.dto.TokensResponse;
import com.noljo.nolzo.auth.jwt.JwtUtil;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.entity.Role;
import com.noljo.nolzo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public RegisterResponse register(RegisterRequest request) {
        validateDuplicateEmail(request.email());

        Member member = Member.of(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.birth(),
                Role.USER
        );

        Member savedMember = memberRepository.save(member);
        return RegisterResponse.from(savedMember);
    }

    public TokensResponse login(LoginRequest request) {
        Member member = findMemberByEmail(request.email());

        // todo: 로그인 실패시 에러 메시지 처리 구체화
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return jwtTokenService.issueToken(member);
    }

    public void logout(String refreshToken) {
        jwtTokenService.removeRefreshTokenByToken(refreshToken);
    }

    public AccessTokenResponse reissueAccessToken(String refreshToken) {
        Long memberId = jwtUtil.getMemberId(refreshToken);
        Member member = memberRepository.getOrThrow(memberId);
        String reissuedAccessToken = jwtTokenService.reissueAccessToken(member, refreshToken);
        return new AccessTokenResponse(reissuedAccessToken);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 입니다."));
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }
}
