package com.noljo.nolzo.auth.dto;

import java.time.LocalDate;

public record RegisterRequest(
        String email,
        String password,
        String name,
        LocalDate birth
) {
}
