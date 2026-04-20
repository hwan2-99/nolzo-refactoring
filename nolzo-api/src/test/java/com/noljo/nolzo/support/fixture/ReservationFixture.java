package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.reservation.entity.Reservation;
import com.noljo.nolzo.reservation.entity.ReservationStatus;
import lombok.Getter;

@Getter
public enum ReservationFixture {

    예약(ReservationStatus.CONFIRMED, 15000, "12313123L"), // 추후 총합가격 로직 추가시 수정
    예약2(ReservationStatus.CONFIRMED, 15000, "1322313123L");

    private ReservationStatus reservationStatus;
    private int totalPrice;
    private String reservationNumber;

    ReservationFixture(ReservationStatus reservationStatus, int totalPrice, String reservationNumber) {
        this.reservationStatus = reservationStatus;
        this.totalPrice = totalPrice;
        this.reservationNumber = reservationNumber;
    }

    public static Reservation 예약(Member member) {
        return new Reservation(예약.reservationStatus, 예약.totalPrice, 예약.reservationNumber, member);
    }

    public static Reservation 예약2(Member member) {
        return new Reservation(null, 예약2.reservationStatus, 예약2.totalPrice, 예약2.reservationNumber, null, member);
    }
}
