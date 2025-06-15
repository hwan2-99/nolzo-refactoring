package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;
import lombok.Getter;

@Getter
public enum SeatFixture {
    일반좌석("B열", 7, "일반", "1층", 12000, SeatStatus.CANCELED);
    private String rowName;
    private int seatNumber;
    private String seatSection;
    private String floor;
    private int price;
    private SeatStatus status;
    private Event event;

    SeatFixture(String rowName, int seatNumber, String seatSection, String floor, int price, SeatStatus status) {
        this.rowName = rowName;
        this.seatNumber = seatNumber;
        this.seatSection = seatSection;
        this.floor = floor;
        this.price = price;
        this.status = status;
    }

    public static Seat 일반좌석(Event event) {
        return new Seat(null, 일반좌석.rowName, 일반좌석.seatNumber, 일반좌석.seatSection, 일반좌석.floor, 일반좌석.price, 일반좌석.status,
                event);
    }
}
