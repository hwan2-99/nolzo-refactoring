package com.noljo.nolzo.notification.adapter.out.event;

import com.noljo.nolzo.notification.application.port.out.PublishSeatAvailableEventPort;
import com.noljo.nolzo.notification.domain.event.SeatAvailableEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeatAvailableEventLoggingAdapter implements PublishSeatAvailableEventPort {

    @Override
    public void publish(SeatAvailableEvent event) {
        log.info("SeatAvailableEvent published: {}", event);
    }
}
