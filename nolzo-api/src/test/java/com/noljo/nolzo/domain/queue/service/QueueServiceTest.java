package com.noljo.nolzo.domain.queue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.queue.application.QueueService;
import com.noljo.nolzo.queue.domain.QueueEntry;
import com.noljo.nolzo.queue.domain.QueueStatus;
import com.noljo.nolzo.queue.repository.QueueEntryRepository;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.MemberFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
class QueueServiceTest {

    @Autowired
    private QueueService queueService;

    @Autowired
    private MemberPersistencePort memberRepository;

    @Autowired
    private QueueEntryRepository queueEntryRepository;

    @Test
    void 예약_성공_후에도_같은_이벤트에_다시_대기열_진입이_가능하다() {
        Member member = memberRepository.save(MemberFixture.회원());
        Long eventId = 1L;

        queueService.validateQueue(eventId, member.getId());
        queueService.markReserved(eventId, member.getId());

        assertThatCode(() -> queueService.validateQueue(eventId, member.getId()))
                .doesNotThrowAnyException();

        QueueEntry queueEntry = queueEntryRepository.findByEventIdAndMemberId(eventId, member.getId())
                .orElseThrow(() -> new AssertionError("대기열 엔트리를 찾을 수 없습니다."));

        assertThat(queueEntry.getStatus()).isEqualTo(QueueStatus.ACTIVE);
    }
}
