package com.noljo.nolzo.member.service;

import com.noljo.nolzo.member.application.port.in.MemberUseCase;
import com.noljo.nolzo.member.dto.PasswordChangeRequest;
import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.application.port.out.MemberPersistencePort;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.noljo.nolzo.member.dto.MemberDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberUseCase {
    private final MemberPersistencePort memberPersistencePort;

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberPersistencePort.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not found."));
        member.softDelete();
    }

    @Transactional
    public void changeMemberPassword(Long memberId, PasswordChangeRequest request) {
        Member member = memberPersistencePort.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not found"));

        member.changePassword(request.getPassword());
    }

    public MemberDto readMember(Long memberId) {
        Member member = memberPersistencePort.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member not found"));
        return MemberDto.from(member);
    }

    public List<MemberDto> readAll() {
        List<Member> members = memberPersistencePort.findAll();
        return members.stream().map(MemberDto::from).toList();
    }
}
