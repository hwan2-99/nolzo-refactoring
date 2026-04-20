package com.noljo.nolzo.auth.application.port.in;

import com.noljo.nolzo.auth.dto.AccessTokenResponse;
import com.noljo.nolzo.auth.dto.LoginRequest;
import com.noljo.nolzo.auth.dto.RegisterRequest;
import com.noljo.nolzo.auth.dto.RegisterResponse;
import com.noljo.nolzo.auth.dto.TokensResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthUseCase {

    RegisterResponse register(RegisterRequest request);

    TokensResponse login(LoginRequest request, String clientIp);

    void logout(String refreshToken);

    AccessTokenResponse reissueAccessToken(String refreshToken);

    String getClientIp(HttpServletRequest request);
}
