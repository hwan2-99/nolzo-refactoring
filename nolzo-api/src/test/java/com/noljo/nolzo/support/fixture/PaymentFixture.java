package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.payment.entity.PaymentMethod;
import com.noljo.nolzo.reservation.entity.Reservation;
import lombok.Getter;

@Getter
public enum PaymentFixture {
    신용카드(PaymentMethod.CREDIT_CARD);

    private PaymentMethod paymentMethod;

    PaymentFixture(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public static Payment 신용카드(Member member, Reservation reservation) {
        return new Payment(PaymentMethod.CREDIT_CARD, member, reservation);
    }
}
