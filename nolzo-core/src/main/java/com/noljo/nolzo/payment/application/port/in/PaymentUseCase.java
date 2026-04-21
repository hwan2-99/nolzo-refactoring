package com.noljo.nolzo.payment.application.port.in;

import com.noljo.nolzo.payment.dto.PaymentRequest;
import com.noljo.nolzo.payment.dto.PaymentResponse;

public interface PaymentUseCase {

    PaymentResponse create(Long userId, PaymentRequest request);
}
