package com.noljo.nolzo.auth.repository;

import com.noljo.nolzo.auth.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByToken(String token);

    Optional<RefreshToken> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}