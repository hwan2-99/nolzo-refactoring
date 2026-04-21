package com.noljo.nolzo.payment.application.port.out;

import com.noljo.nolzo.payment.entity.Payment;
import java.util.List;

public interface PaymentPersistencePort {

    <S extends Payment> S save(S payment);

    List<Payment> findAll();

    Payment findPaymentByMemberIdAndReservationId(Long memberId, Long reservationId);
}
