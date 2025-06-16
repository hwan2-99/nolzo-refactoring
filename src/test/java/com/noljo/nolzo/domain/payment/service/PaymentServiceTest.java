package com.noljo.nolzo.domain.payment.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.payment.repository.PaymentRepository;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.MemberFixture;
import com.noljo.nolzo.support.fixture.PaymentFixture;
import com.noljo.nolzo.support.fixture.ReservationFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
public class PaymentServiceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void 예약과_유저를_통해_결제를_할_수_있다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);

        Reservation reservation = ReservationFixture.예약(member);
        reservationRepository.save(reservation);

        paymentRepository.save(PaymentFixture.신용카드(member, reservation));
        assertThat(paymentRepository.findAll()).hasSize(1);
    }
}
