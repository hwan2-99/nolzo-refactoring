package com.noljo.nolzo.member.controller;

import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.member.dto.PasswordChangeRequest;
import com.noljo.nolzo.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/password")
  public ResponseEntity<?> changePassword(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid PasswordChangeRequest request) {
    Long memberId = userDetails.getMemberId();
    memberService.changeMemberPassword(memberId, request);

    return ResponseEntity.ok("password change successful.");
  }

}