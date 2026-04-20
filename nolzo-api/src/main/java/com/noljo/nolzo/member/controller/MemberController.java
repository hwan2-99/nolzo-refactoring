package com.noljo.nolzo.member.controller;

import com.noljo.nolzo.member.service.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.noljo.nolzo.member.dto.PasswordChangeRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.noljo.nolzo.member.dto.MemberDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/readAll")
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        return ResponseEntity.ok(memberService.readAll());
    }

    @DeleteMapping
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal(expression = "memberId") Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.ok("delete successful.");
    }

    @PostMapping("/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @RequestBody @Valid PasswordChangeRequest request) {
        memberService.changeMemberPassword(memberId, request);

        return ResponseEntity.ok("password change successful.");
    }

    @GetMapping
    public ResponseEntity<MemberDto> getMember(@AuthenticationPrincipal(expression = "memberId") Long memberId) {
        return ResponseEntity.ok(memberService.readMember(memberId));
    }
}


