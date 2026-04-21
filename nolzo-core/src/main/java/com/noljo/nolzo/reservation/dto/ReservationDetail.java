package com.noljo.nolzo.reservation.dto;

import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.payment.entity.PaymentMethod;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.ticket.dto.TicketResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationDetail {

    private Long id;
    private List<TicketResponse> tickets;
    private String status;
    private int totalPrice;
    private String reservationNumber;
    private LocalDateTime createdAt;
    private PaymentMethod paymentMethod;


    public static ReservationDetail from(Reservation reservation) {
        return ReservationDetail.builder()
                .id(reservation.getId())
                .tickets(mapTickets(reservation))
                .status(reservation.getStatus().name())
                .totalPrice(reservation.getTotalPrice())
                .reservationNumber(reservation.getReservationNumber())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    public static ReservationDetail fromDetails(Reservation reservation, Payment payment) {
        return ReservationDetail.builder()
                .id(reservation.getId())
                .tickets(mapTickets(reservation))
                .status(reservation.getStatus().name())
                .totalPrice(reservation.getTotalPrice())
                .reservationNumber(reservation.getReservationNumber())
                .createdAt(reservation.getCreatedAt())
                .paymentMethod(payment.getPaymentMethod())
                .build();
    }

    private static List<TicketResponse> mapTickets(Reservation reservation) {
        return reservation.getTickets().stream()
                .map(TicketResponse::from)
                .toList();
    }
}