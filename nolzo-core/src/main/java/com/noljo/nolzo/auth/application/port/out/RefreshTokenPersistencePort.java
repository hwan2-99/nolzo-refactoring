package com.noljo.nolzo.auth.application.port.out;

import com.noljo.nolzo.auth.entity.RefreshToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenPersistencePort {

    RefreshToken findByToken(String token);

    Optional<RefreshToken> findByMemberId(Long memberId);

    <S extends RefreshToken> S save(S refreshToken);

    <S extends RefreshToken> List<S> saveAll(Iterable<S> refreshTokens);

    List<RefreshToken> findAll();

    void delete(RefreshToken refreshToken);

    void deleteByMemberId(Long memberId);

    void deleteAllByExpiryDateBefore(LocalDateTime now);
}
