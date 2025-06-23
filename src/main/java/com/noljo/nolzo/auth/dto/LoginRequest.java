package com.noljo.nolzo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @Email
        String email,

        @NotNull
        String password
) {
}
