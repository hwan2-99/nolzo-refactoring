package com.noljo.nolzo.auth.adapter.out.persistence;

import com.noljo.nolzo.auth.application.port.out.RefreshTokenPersistencePort;
import com.noljo.nolzo.auth.entity.RefreshToken;
import com.noljo.nolzo.auth.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenPersistencePort {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public Optional<RefreshToken> findByMemberId(Long memberId) {
        return refreshTokenRepository.findByMemberId(memberId);
    }

    @Override
    public <S extends RefreshToken> S save(S refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public <S extends RefreshToken> List<S> saveAll(Iterable<S> refreshTokens) {
        return refreshTokenRepository.saveAll(refreshTokens);
    }

    @Override
    public List<RefreshToken> findAll() {
        return refreshTokenRepository.findAll();
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }

    @Override
    public void deleteAllByExpiryDateBefore(LocalDateTime now) {
        refreshTokenRepository.deleteAllByExpiryDateBefore(now);
    }
}
