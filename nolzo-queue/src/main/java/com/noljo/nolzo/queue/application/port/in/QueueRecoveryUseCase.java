package com.noljo.nolzo.queue.application.port.in;

public interface QueueRecoveryUseCase {

    void rebuildRedisFromDb();
}
