package com.noljo.nolzo.auth.repository;

import com.noljo.nolzo.auth.entity.RefreshToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("SELECT r FROM RefreshToken r WHERE r.refreshToken = :token")
    RefreshToken findByToken(@Param("token") String token);


    Optional<RefreshToken> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);

    void deleteAllByExpiryDateBefore(LocalDateTime now);
}
