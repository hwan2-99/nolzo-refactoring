package com.noljo.nolzo.member.controller;

import com.noljo.nolzo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

  private final MemberService memberService;

  @DeleteMapping
  public ResponseEntity<?> changePassword(
      @AuthenticationPrincipal(expression = "memberId") Long memberId
      ) {
    memberService.deleteMember(memberId);
    return ResponseEntity.ok("delete successful.");
  }

}