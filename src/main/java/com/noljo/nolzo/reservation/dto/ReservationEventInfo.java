package com.noljo.nolzo.reservation.dto;

import com.noljo.nolzo.event.dto.ReservationEvent;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.reservation.entity.Reservation;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationEventInfo {
    private ReservationEvent event;
    private ReservationDetail detail;

    public static ReservationEventInfo of(Event event, Reservation reservation) {
        return ReservationEventInfo.builder()
                .event(ReservationEvent.from(event))
                .detail(ReservationDetail.from(reservation))
                .build();
    }
}
