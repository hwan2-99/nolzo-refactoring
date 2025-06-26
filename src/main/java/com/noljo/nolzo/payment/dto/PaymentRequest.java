package com.noljo.nolzo.payment.dto;

import com.noljo.nolzo.payment.entity.PaymentMethod;

public record PaymentRequest(Long memberId, Long reservationId, PaymentMethod paymentMethod, String paymentStatus) {
}
