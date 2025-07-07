package com.noljo.nolzo.auth.controller;

import com.noljo.nolzo.auth.dto.AccessTokenResponse;
import com.noljo.nolzo.auth.dto.LoginRequest;
import com.noljo.nolzo.auth.dto.RegisterRequest;
import com.noljo.nolzo.auth.dto.RegisterResponse;
import com.noljo.nolzo.auth.dto.TokensResponse;
import com.noljo.nolzo.auth.service.AuthService;
import com.noljo.nolzo.auth.service.JwtTokenService;
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

    private static final String REFRESH_TOKEN = "refreshToken";
    private final AuthService authService;
    private final JwtTokenService jwtTokenService;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokensResponse> login(@Valid @RequestBody LoginRequest request,
                                                HttpServletResponse response) {
        TokensResponse tokens = authService.login(request);
        addRefreshTokenCookie(response, tokens.refreshToken(), Duration.ofSeconds(refreshTokenValidityInSeconds));
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = REFRESH_TOKEN, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }
        clearRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissueAccessToken(
            @CookieValue(REFRESH_TOKEN) String refreshToken,
            HttpServletResponse response
    ) {
        String accessToken = authService.reissueAccessToken(refreshToken).accessToken();
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(accessToken);
        return ResponseEntity.ok(accessTokenResponse);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String value, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN, value)
                .httpOnly(true)
                .secure(false) // 이후 https 사용시 true로 전환
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        addRefreshTokenCookie(response, "", Duration.ZERO);
    }
}
