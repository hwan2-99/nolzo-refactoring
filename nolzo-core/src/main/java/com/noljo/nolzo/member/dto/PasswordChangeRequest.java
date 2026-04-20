package com.noljo.nolzo.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordChangeRequest {
  @NotBlank
  @Size(min = 8)
  private String password;
}
