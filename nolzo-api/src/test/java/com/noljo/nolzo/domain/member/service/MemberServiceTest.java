package com.noljo.nolzo.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.MemberFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
public class MemberServiceTest {
    @Autowired
    private MemberPersistencePort memberRepository;

    @Test
    void 회원은_저장_가능하다() {
        Member member = MemberFixture.회원();
        memberRepository.save(member);
        assertThat(memberRepository.findAll()).hasSize(1);
    }
}
