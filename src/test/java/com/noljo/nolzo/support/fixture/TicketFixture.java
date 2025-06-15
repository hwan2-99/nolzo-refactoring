package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.ticket.entity.Ticket;
import com.noljo.nolzo.ticket.entity.TicketStatus;
import lombok.Getter;

@Getter
public enum TicketFixture {
    미사용티켓(TicketStatus.NOT_USED);

    private TicketStatus status;

    TicketFixture(TicketStatus status) {
        this.status = status;
    }

    public static Ticket 미사용티켓(Reservation reservation, Seat seat) {
        return new Ticket(null, 미사용티켓.status, reservation, seat);
    }
}
