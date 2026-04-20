package com.noljo.nolzo.payment.controller;

import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.payment.dto.PaymentRequest;
import com.noljo.nolzo.payment.dto.PaymentResponse;
import com.noljo.nolzo.payment.service.PaymentService;
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
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@AuthenticationPrincipal CustomUserDetails user,
                                                         @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.create(user.getMemberId(), request);
        return ResponseEntity.ok(response);
    }
}
