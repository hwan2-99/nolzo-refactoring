package com.noljo.nolzo.auth.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

    private final Map<Long, String> refreshTokenStore = new ConcurrentHashMap<>();

    @Override
    public void save(Long memberId, String refreshToken) {
        refreshTokenStore.put(memberId, refreshToken);
    }

    @Override
    public String findByMemberId(Long memberId) {
        return refreshTokenStore.get(memberId);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        refreshTokenStore.remove(memberId);
    }
}
