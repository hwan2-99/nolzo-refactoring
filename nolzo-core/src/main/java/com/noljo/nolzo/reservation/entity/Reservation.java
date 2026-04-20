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
@Table(
        name = "reservation",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_reservation_idempotency_key", columnNames = {"idempotency_key"})
        }
)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private int totalPrice;

    private String reservationNumber;

    @Column(name = "idempotency_key", updatable = false, length = 64)
    private String idempotencyKey;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.REMOVE)
    private List<Ticket> tickets = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public Reservation(Long id, ReservationStatus status, int totalPrice, String reservationNumber,
                       String idempotencyKey, Member member) {
        this.id = id;
        this.status = status;
        this.totalPrice = totalPrice;
        this.reservationNumber = reservationNumber;
        this.idempotencyKey = idempotencyKey;
        this.member = member;
    }

    public Reservation(ReservationStatus status, int totalPrice, String reservationNumber,
                       Member member, String idempotencyKey) {
        this(null, status, totalPrice, reservationNumber, idempotencyKey, member);
    }

    public Reservation(ReservationStatus status, int totalPrice, String reservationNumber,
                       Member member) {
        this(null, status, totalPrice, reservationNumber, null, member);
    }

    public void updateStatus(ReservationStatus reservationStatus) {
        this.status = reservationStatus;
    }

    public void cancelAllTickets() {
        this.tickets.forEach(Ticket::cancel);
    }
}
