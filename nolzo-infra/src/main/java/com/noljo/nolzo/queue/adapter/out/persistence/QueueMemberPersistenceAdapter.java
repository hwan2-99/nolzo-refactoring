package com.noljo.nolzo.queue.adapter.out.persistence;

import com.noljo.nolzo.queue.application.port.out.QueueMemberLookupPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueMemberPersistenceAdapter implements QueueMemberLookupPort {

    private final com.noljo.nolzo.member.application.port.out.MemberPersistencePort memberPort;

    @Override
    public void ensureExists(Long memberId) {
        memberPort.getOrThrow(memberId);
    }
}
