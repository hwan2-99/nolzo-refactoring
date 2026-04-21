package com.noljo.nolzo.payment.controller;

import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.payment.application.port.in.PaymentUseCase;
import com.noljo.nolzo.payment.dto.PaymentRequest;
import com.noljo.nolzo.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentUseCase paymentUseCase;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@AuthenticationPrincipal CustomUserDetails user,
                                                         @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentUseCase.create(user.getMemberId(), request);
        return ResponseEntity.ok(response);
    }
}
