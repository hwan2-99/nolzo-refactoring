package com.noljo.nolzo.queue.adapter.out.redis;

import org.springframework.stereotype.Component;

@Component
public class QueueKeyGenerator {

    public String queueKey(Long eventId) {
        return "queue:reservation:event:" + eventId;
    }

    public String activeKey(Long eventId) {
        return "active:reservation:event:" + eventId;
    }

    public String enterKey(Long eventId, Long memberId) {
        return "queue:reservation:enter:" + eventId + ":" + memberId;
    }

    public String managedEventsKey() {
        return "queue:reservation:managed:events";
    }
}
