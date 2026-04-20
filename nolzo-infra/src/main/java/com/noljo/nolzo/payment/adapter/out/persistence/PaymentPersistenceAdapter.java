package com.noljo.nolzo.payment.adapter.out.persistence;

import com.noljo.nolzo.payment.application.port.out.PaymentPersistencePort;
import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.payment.repository.PaymentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements PaymentPersistencePort {

    private final PaymentRepository paymentRepository;

    @Override
    public <S extends Payment> S save(S payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment findPaymentByMemberIdAndReservationId(Long memberId, Long reservationId) {
        return paymentRepository.findPaymentByMemberIdAndReservationId(memberId, reservationId);
    }
}
