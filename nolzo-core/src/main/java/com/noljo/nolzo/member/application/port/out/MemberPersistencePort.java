package com.noljo.nolzo.member.application.port.out;

import com.noljo.nolzo.member.entity.Member;
import java.util.List;
import java.util.Optional;

public interface MemberPersistencePort {

    Optional<Member> findById(Long id);

    List<Member> findAll();

    <S extends Member> S save(S member);

    Optional<Member> findByEmailAndPassword(String email, String password);

    default Member getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found"));
    }

    Optional<Member> findByEmail(String email);
}
