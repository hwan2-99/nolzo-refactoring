package com.noljo.nolzo.member.service;

import com.noljo.nolzo.member.dto.MemberDto;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
  private final MemberRepository memberRepository;

  public MemberDto readMember(Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("member not found"));
    return MemberDto.from(member);
  }
}
