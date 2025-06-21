package com.noljo.nolzo.member.controller;

import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.member.dto.MemberDto;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

  private final MemberService memberService;

  @GetMapping
  public ResponseEntity<MemberDto> getMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
    Long memberId = userDetails.getMemberId();
    return ResponseEntity.ok(memberService.readMember(memberId));
  }
}
