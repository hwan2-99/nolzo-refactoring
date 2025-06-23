package com.noljo.nolzo.reservation.entity;

import com.noljo.nolzo.global.BaseEntity;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.ticket.entity.Ticket;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private int totalPrice;

    private String reservationNumber;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.PERSIST)
    private List<Ticket> tickets = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public Reservation(Long id, ReservationStatus status, int totalPrice, String reservationNumber,
                       Member member) {
        this.id = id;
        this.status = status;
        this.totalPrice = totalPrice;
        this.reservationNumber = reservationNumber;
        this.member = member;
    }

    public Reservation(ReservationStatus status, int totalPrice, String reservationNumber,
                       Member member) {
        this(null, status, totalPrice, reservationNumber, member);
    }
}
