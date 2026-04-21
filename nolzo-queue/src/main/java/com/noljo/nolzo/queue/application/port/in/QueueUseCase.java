package com.noljo.nolzo.queue.application.port.in;

import java.util.Set;

public interface QueueUseCase {

    void validateQueue(Long eventId, Long memberId);

    void leaveEntrance(Long eventId, Long memberId);

    void markReserved(Long eventId, Long memberId);

    void processQueue(Long eventId);

    Set<Long> getManagedEventIds();
}
