package com.noljo.nolzo.outbox.application.port.out;

import com.noljo.nolzo.outbox.domain.OutboxEvent;
import java.util.List;

public interface LoadOutboxEventPort {

    List<OutboxEvent> findPublishableEvents(int limit);
}
