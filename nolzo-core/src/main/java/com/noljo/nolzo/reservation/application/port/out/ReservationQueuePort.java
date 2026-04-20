package com.noljo.nolzo.reservation.application.port.out;

public interface ReservationQueuePort {

    void validateQueue(Long eventId, Long memberId);

    void markReserved(Long eventId, Long memberId);

    void leaveEntrance(Long eventId, Long memberId);
}
