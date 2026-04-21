package com.noljo.nolzo.member.controller;

import com.noljo.nolzo.member.application.port.in.MemberUseCase;
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

    private final MemberUseCase memberUseCase;

    @GetMapping("/readAll")
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        return ResponseEntity.ok(memberUseCase.readAll());
    }

    @DeleteMapping
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal(expression = "memberId") Long memberId) {
        memberUseCase.deleteMember(memberId);
        return ResponseEntity.ok("delete successful.");
    }

    @PostMapping("/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @RequestBody @Valid PasswordChangeRequest request) {
        memberUseCase.changeMemberPassword(memberId, request);

        return ResponseEntity.ok("password change successful.");
    }

    @GetMapping
    public ResponseEntity<MemberDto> getMember(@AuthenticationPrincipal(expression = "memberId") Long memberId) {
        return ResponseEntity.ok(memberUseCase.readMember(memberId));
    }
}
