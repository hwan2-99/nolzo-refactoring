package com.noljo.nolzo.member.application.port.in;

import com.noljo.nolzo.member.dto.MemberDto;
import com.noljo.nolzo.member.dto.PasswordChangeRequest;
import java.util.List;

public interface MemberUseCase {

    void deleteMember(Long memberId);

    void changeMemberPassword(Long memberId, PasswordChangeRequest request);

    MemberDto readMember(Long memberId);

    List<MemberDto> readAll();
}
