package com.noljo.nolzo.member.dto;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MemberDto {

  private Long id;
  private String name;
  private String email;
  private LocalDate birth;
  private Role role;

  public static MemberDto from(Member member) {
    return MemberDto.builder()
        .id(member.getId())
        .name(member.getName())
        .email(member.getEmail())
        .birth(member.getBirth())
        .role(member.getRole())
        .build();
  }
}