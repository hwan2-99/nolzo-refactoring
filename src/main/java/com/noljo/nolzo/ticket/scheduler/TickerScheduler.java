package com.noljo.nolzo.ticket.scheduler;

import com.noljo.nolzo.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class TickerScheduler {

    private final TicketService ticketService;

    @Scheduled(fixedRate = 1800 )
    public void changeUsed() {
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        ticketService.updateTicketStatusUsed(today,currentTime);
    }
}
