package com.noljo.nolzo.reservation.adapter.out.queue;

import com.noljo.nolzo.queue.application.QueueService;
import com.noljo.nolzo.reservation.application.port.out.ReservationQueuePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueReservationAdapter implements ReservationQueuePort {

    private final QueueService queueService;

    @Override
    public void validateQueue(Long eventId, Long memberId) {
        queueService.validateQueue(eventId, memberId);
    }

    @Override
    public void markReserved(Long eventId, Long memberId) {
        queueService.markReserved(eventId, memberId);
    }

    @Override
    public void leaveEntrance(Long eventId, Long memberId) {
        queueService.leaveEntrance(eventId, memberId);
    }
}
