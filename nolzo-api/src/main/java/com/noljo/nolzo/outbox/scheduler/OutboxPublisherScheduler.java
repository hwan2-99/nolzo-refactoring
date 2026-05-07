package com.noljo.nolzo.outbox.scheduler;

import com.noljo.nolzo.outbox.service.OutboxPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisherScheduler {

    private final OutboxPublisherService outboxPublisherService;

    @Scheduled(fixedDelayString = "${app.outbox.publish.fixed-delay-ms:3000}")
    @SchedulerLock(name = "outboxPublisherScheduler", lockAtMostFor = "PT30S", lockAtLeastFor = "PT1S")
    public void publish() {
        log.debug("Outbox publish scheduler triggered.");
        outboxPublisherService.publishPendingEvents();
    }
}
