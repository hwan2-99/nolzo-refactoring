package com.noljo.nolzo.payment.dto;

import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.payment.entity.PaymentMethod;

public record PaymentResponse(Long id, int price, PaymentMethod paymentMethod) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getPrice(), payment.getPaymentMethod());
    }
}
