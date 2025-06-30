package com.noljo.nolzo.reservation.dto;

import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.payment.entity.PaymentMethod;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.seat.dto.SeatResponse;
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

    private Long id;
    private List<SeatResponse> seats;
    private String status;
    private int totalPrice;
    private String reservationNumber;
    private LocalDateTime createdAt;
    private PaymentMethod paymentMethod;


    public static ReservationDetail from(Reservation reservation) {
        List<SeatResponse> reservedSeats = reservation.getTickets().stream()
                .map(Ticket::getSeat)
                .map(SeatResponse::from)
                .collect(toList());

        return ReservationDetail.builder()
                .id(reservation.getId())
                .seats(reservedSeats)
                .status(reservation.getStatus().name())
                .totalPrice(reservation.getTotalPrice())
                .reservationNumber(reservation.getReservationNumber())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    public static ReservationDetail fromDetails(Reservation reservation, Payment payment) {
        List<SeatResponse> reservedSeats = reservation.getTickets().stream()
                .map(Ticket::getSeat)
                .map(SeatResponse::from)
                .collect(toList());

        return ReservationDetail.builder()
                .id(reservation.getId())
                .seats(reservedSeats)
                .status(reservation.getStatus().name())
                .totalPrice(reservation.getTotalPrice())
                .reservationNumber(reservation.getReservationNumber())
                .createdAt(reservation.getCreatedAt())
                .paymentMethod(payment.getPaymentMethod())
                .build();
    }
}

