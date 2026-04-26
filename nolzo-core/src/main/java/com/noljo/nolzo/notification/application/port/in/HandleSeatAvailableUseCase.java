package com.noljo.nolzo.notification.application.port.in;

import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;

public interface HandleSeatAvailableUseCase {

    void handle(SeatAvailableEvent event);
}
