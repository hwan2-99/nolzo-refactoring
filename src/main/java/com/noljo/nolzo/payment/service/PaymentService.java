package com.noljo.nolzo.payment.service;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.payment.dto.PaymentRequest;
import com.noljo.nolzo.payment.dto.PaymentResponse;
import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.payment.repository.PaymentRepository;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    public PaymentResponse create(PaymentRequest request) {
        Member member = memberRepository.getOrThrow(request.memberId());
        Reservation reservation = reservationRepository.getOrThrow(request.reservationId());

        Payment payment = paymentRepository.save(new Payment(request.paymentMethod(), member, reservation));
        return PaymentResponse.from(payment);
    }
}
