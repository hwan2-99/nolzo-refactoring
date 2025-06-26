package com.noljo.nolzo.payment.entity;

import com.noljo.nolzo.global.BaseEntity;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    private int price;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    public Payment(PaymentMethod paymentMethod, Member member, Reservation reservation) {
        this.price = reservation.getTotalPrice();
        this.paymentMethod = paymentMethod;
        this.member = member;
        this.reservation = reservation;
    }
}
