package com.noljo.nolzo.reservation.dto;

import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.ticket.entity.Ticket;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Builder
public class ReservationDetail {

    private List<Seat> seats;
    private String status;
    private int totalPrice;
    private String reservationNumber;
    private LocalDateTime createdAt;


    public static ReservationDetail from(Reservation reservation) {
        List<Seat> reservedSeats = reservation.getTickets().stream()
                .map(Ticket::getSeat)
                .collect(toList());

        return ReservationDetail.builder()
                .seats(reservedSeats)
                .status(reservation.getStatus().name())
                .totalPrice(reservation.getTotalPrice())
                .reservationNumber(reservation.getReservationNumber())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
