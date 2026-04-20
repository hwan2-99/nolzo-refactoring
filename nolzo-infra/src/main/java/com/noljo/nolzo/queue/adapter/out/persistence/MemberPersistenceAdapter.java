package com.noljo.nolzo.queue.adapter.out.persistence;

import com.noljo.nolzo.member.repository.MemberRepository;
import com.noljo.nolzo.queue.application.port.out.MemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberPort {

    private final MemberRepository memberRepository;

    @Override
    public void ensureExists(Long memberId) {
        memberRepository.getOrThrow(memberId);
    }
}
