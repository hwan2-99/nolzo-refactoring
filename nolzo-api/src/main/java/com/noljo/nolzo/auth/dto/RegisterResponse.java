package com.noljo.nolzo.auth.dto;

import com.noljo.nolzo.member.entity.Member;

public record RegisterResponse (
        Long memberId,
        String name
) {
    public static RegisterResponse from(Member member) {
        return new RegisterResponse(member.getId(), member.getName());
    }
}
