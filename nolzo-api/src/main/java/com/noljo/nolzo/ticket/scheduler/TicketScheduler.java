package com.noljo.nolzo.ticket.scheduler;

import com.noljo.nolzo.ticket.application.port.in.TicketUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class TicketScheduler {

    private final TicketUseCase ticketUseCase;

    @Scheduled(fixedRate = 1800000)
    public void changeUsed() {
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        ticketUseCase.updateTicketStatusUsed(today,currentTime);
    }
}
