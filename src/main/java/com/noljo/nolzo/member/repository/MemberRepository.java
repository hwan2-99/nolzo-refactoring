package com.noljo.nolzo.member.repository;

import com.noljo.nolzo.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    default Member getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("not found")); // 에러코드 추후 통일화 필요
    }
}
