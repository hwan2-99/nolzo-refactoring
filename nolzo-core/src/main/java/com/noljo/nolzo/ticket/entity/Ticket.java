package com.noljo.nolzo.ticket.entity;

import com.noljo.nolzo.global.BaseEntity;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Builder
    public Ticket(Long id, TicketStatus status, Reservation reservation, Seat seat) {
        this.id = id;
        this.status = status;
        this.reservation = reservation;
        this.seat = seat;
    }

    public Ticket(TicketStatus status, Reservation reservation, Seat seat) {
        this.status = status;
        this.reservation = reservation;
        this.seat = seat;
    }

    public void cancel() {
        this.status = TicketStatus.CANCELLED;
        seat.updateStatus(SeatStatus.AVAILABLE);
    }
}
