package com.noljo.nolzo.auth.service;

import com.noljo.nolzo.auth.dto.RegisterRequest;
import com.noljo.nolzo.auth.dto.RegisterResponse;
import com.noljo.nolzo.auth.jwt.JwtUtil;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.entity.Role;
import com.noljo.nolzo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

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
}
