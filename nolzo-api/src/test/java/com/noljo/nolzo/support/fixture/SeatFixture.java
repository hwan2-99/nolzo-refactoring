package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.seat.entity.Seat;
import com.noljo.nolzo.seat.entity.SeatStatus;
import lombok.Getter;

@Getter
public enum SeatFixture {
    일반좌석("B열", 7, "일반", "1층", 12000, SeatStatus.AVAILABLE),
    VIP좌석("A열", 1, "VIP", "2층", 50000, SeatStatus.RESERVED),
    프리미엄좌석("C열", 5, "프리미엄", "1층", 30000, SeatStatus.AVAILABLE),
    일반좌석2("D열", 10, "일반", "3층", 15000, SeatStatus.AVAILABLE),
    스탠딩("S열", 1, "스탠딩", "입구", 10000, SeatStatus.AVAILABLE);

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

    public static Seat 일반좌석(Schedule schedule) {
        return new Seat(null, 일반좌석.rowName, 일반좌석.seatNumber, 일반좌석.seatSection, 일반좌석.floor, 일반좌석.price, 일반좌석.status,
                schedule);
    }

    public static Seat VIP좌석(Schedule schedule) {
        return new Seat(null, VIP좌석.rowName, VIP좌석.seatNumber, VIP좌석.seatSection, VIP좌석.floor, VIP좌석.price,
                VIP좌석.status, schedule);
    }

    public static Seat 프리미엄좌석(Schedule schedule) {
        return new Seat(null, 프리미엄좌석.rowName, 프리미엄좌석.seatNumber, 프리미엄좌석.seatSection, 프리미엄좌석.floor, 프리미엄좌석.price,
                프리미엄좌석.status, schedule);
    }

    public static Seat 일반좌석2(Schedule schedule) {
        return new Seat(null, 일반좌석2.rowName, 일반좌석2.seatNumber, 일반좌석2.seatSection, 일반좌석2.floor, 일반좌석2.price,
                일반좌석2.status, schedule);
    }

    public static Seat 스탠딩(Schedule schedule) {
        return new Seat(null, 스탠딩.rowName, 스탠딩.seatNumber, 스탠딩.seatSection, 스탠딩.floor, 스탠딩.price, 스탠딩.status, schedule);
    }
}
