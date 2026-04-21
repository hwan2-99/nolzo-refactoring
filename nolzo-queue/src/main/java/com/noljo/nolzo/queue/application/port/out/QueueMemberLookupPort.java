package com.noljo.nolzo.queue.application.port.out;

public interface QueueMemberLookupPort {

    void ensureExists(Long memberId);
}
