package com.noljo.nolzo.payment.service;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.payment.dto.PaymentRequest;
import com.noljo.nolzo.payment.dto.PaymentResponse;
import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.payment.repository.PaymentRepository;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.reservation.service.ReservationService;
import com.noljo.nolzo.seat.entity.SeatStatus;
import com.noljo.nolzo.seat.service.SeatService;
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
    private final ReservationService reservationService;
    private final SeatService seatService;

    //Todo 추루 트랜잭션 분리예정
    public PaymentResponse create(Long userId, PaymentRequest request) {
        Member member = memberRepository.getOrThrow(userId);
        Reservation reservation = reservationRepository.getOrThrow(request.reservationId());
        if (isCanceled(request)) {
            seatService.updateWithPayment(reservation.getTickets(), SeatStatus.AVAILABLE);
            reservationRepository.delete(reservation);
            return null;
        }
        Payment payment = paymentRepository.save(new Payment(request.paymentMethod(), member, reservation));
        reservation.updateStatus(ReservationStatus.CONFIRMED);
        seatService.updateWithPayment(reservation.getTickets(), SeatStatus.RESERVED);
        return PaymentResponse.from(payment);
    }

    private boolean isCanceled(PaymentRequest request) {
        return !request.paymentStatus().equals("SUCCESS");
    }
}
