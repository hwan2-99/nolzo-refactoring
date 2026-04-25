package com.noljo.nolzo.notification.application.port.out;

import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;

public interface PublishSeatAvailableEventPort {

    void publish(SeatAvailableEvent event);
}
