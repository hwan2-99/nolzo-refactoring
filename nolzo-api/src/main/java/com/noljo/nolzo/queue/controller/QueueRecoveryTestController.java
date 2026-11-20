package com.noljo.nolzo.queue.controller;

import com.noljo.nolzo.queue.application.port.in.QueueRecoveryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/queue")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "queue.recovery-test.enabled", havingValue = "true")
public class QueueRecoveryTestController {

    private final QueueRecoveryUseCase queueRecoveryUseCase;

    @PostMapping("/recover")
    public ResponseEntity<Void> recover() {
        queueRecoveryUseCase.rebuildRedisFromDb();
        return ResponseEntity.noContent().build();
    }
}
