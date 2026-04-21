package com.noljo.nolzo.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token", indexes = {
        @Index(name = "idx_refresh_token_token", columnList = "refresh_token"),
        @Index(name = "idx_refresh_token_member_id", columnList = "member_id")
})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String refreshToken;

    @Column
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public RefreshToken(Long memberId, String refreshToken, LocalDateTime expiryDate) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
        this.expiryDate = expiryDate;
    }

    public RefreshToken(Long memberId, String refreshToken, LocalDateTime expiryDate, String ipAddress) {
        this(memberId, refreshToken, expiryDate);
        this.ipAddress = ipAddress;
    }

    public void updateToken(String token, LocalDateTime expiryDate) {
        this.refreshToken = token;
        this.expiryDate = expiryDate;
    }
}
