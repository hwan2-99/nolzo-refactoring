package com.noljo.nolzo.member.service;

import com.noljo.nolzo.member.dto.PasswordChangeRequest;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public void changeMemberPassword(Long memberId, PasswordChangeRequest request) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("member not found"));

    member.changePassword(passwordEncoder.encode(request.getPassword()));
  }

}