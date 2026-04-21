package com.noljo.nolzo.seat.application.port.in;

import com.noljo.nolzo.seat.dto.SeatResponse;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;
import com.noljo.nolzo.ticket.entity.Ticket;
import java.util.List;

public interface SeatUseCase {

    List<SeatResponse> createSeats(Long scheduleId);

    void updateWithReservation(List<Seat> seats);

    void updateWithPayment(List<Ticket> tickets, SeatStatus seatStatus);

    void updateWithRedisson(List<Long> seatIds);

    int calculateTotalPrice(List<Long> seatIds);

    List<SeatResponse> findSeats(Long eventId, String date, String time);
}
