package com.noljo.nolzo.auth.controller;

import com.noljo.nolzo.auth.dto.LoginRequest;
import com.noljo.nolzo.auth.dto.RegisterRequest;
import com.noljo.nolzo.auth.dto.RegisterResponse;
import com.noljo.nolzo.auth.dto.TokensResponse;
import com.noljo.nolzo.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInSeconds;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokensResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        TokensResponse tokens = authService.login(request);

        addCookie(response, ACCESS_TOKEN, tokens.accessToken(), Duration.ofSeconds(accessTokenValidityInSeconds), false);
        addCookie(response, REFRESH_TOKEN, tokens.refreshToken(), Duration.ofSeconds(refreshTokenValidityInSeconds), true);

        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = REFRESH_TOKEN, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken);

        clearCookie(response, ACCESS_TOKEN, false);
        clearCookie(response, REFRESH_TOKEN, true);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reissue")
    public ResponseEntity<Void> reissueAccessToken(
            @CookieValue(REFRESH_TOKEN) String refreshToken,
            HttpServletResponse response
    ) {
        String newAccessToken = authService.reissueAccessToken(refreshToken).accessToken();
        addCookie(response, ACCESS_TOKEN, newAccessToken, Duration.ofSeconds(accessTokenValidityInSeconds), false);
        return ResponseEntity.noContent().build();
    }

    private void addCookie(HttpServletResponse response, String name, String value, Duration maxAge, boolean httpOnly) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(false) // 이후 https 사용시 true로 전환
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearCookie(HttpServletResponse response, String name, boolean httpOnly) {
        addCookie(response, name, "", Duration.ZERO, httpOnly);
    }
}
