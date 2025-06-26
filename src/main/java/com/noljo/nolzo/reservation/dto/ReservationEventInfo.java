package com.noljo.nolzo.reservation.dto;

import com.noljo.nolzo.Schedule.entity.Schedule;
import com.noljo.nolzo.event.dto.ReservationEvent;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.reservation.entity.Reservation;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationEventInfo {
    private ReservationEvent event;
    private ReservationDetail detail;

    public static ReservationEventInfo of(Event event, Reservation reservation) {
        Schedule schedule = event.getSchedules()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트에 등록된 스케줄이 없습니다."));

        return ReservationEventInfo.builder()
                .event(ReservationEvent.from(event, schedule))
                .detail(ReservationDetail.from(reservation))
                .build();
    }

    public static ReservationEventInfo detailsOf(Event event, Reservation reservation,Payment payment) {
        Schedule schedule = event.getSchedules()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트에 등록된 스케줄이 없습니다."));

        return ReservationEventInfo.builder()
                .event(ReservationEvent.fromDetails(event, schedule))
                .detail(ReservationDetail.fromDetails(reservation,payment))
                .build();
    }
}
