package com.noljo.nolzo.payment.repository;

import com.noljo.nolzo.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
            SELECT p from Payment p
            WHERE p.member.id = :memberId
             AND p.reservation.id = :reservationId
            """)
    Payment findPaymentByMemberIdAndReservationId(@Param("memberId") Long memberId,
                                                  @Param("reservationId") Long reservationId);
}
