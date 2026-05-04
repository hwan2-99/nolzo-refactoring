package com.noljo.nolzo.outbox.application.port.out;

import com.noljo.nolzo.outbox.domain.OutboxEvent;

public interface SaveOutboxEventPort {

    <S extends OutboxEvent> S save(S outboxEvent);
}
