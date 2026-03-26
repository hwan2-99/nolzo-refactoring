package com.noljo.nolzo.reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "reservation_queue_entry",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_queue_event_member",
                        columnNames = {"event_id", "member_id"}
                )
        },
        indexes = {
                @Index(name = "idx_queue_event_status", columnList = "event_id, status"),
                @Index(name = "idx_queue_active_until", columnList = "active_until")
        }
)
public class ReservationQueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QueueStatus status;

    @Column(name = "queued_at")
    private LocalDateTime queuedAt;

    @Column(name = "active_until")
    private LocalDateTime activeUntil;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ReservationQueueEntry(Long eventId, Long memberId, QueueStatus status,
                                 LocalDateTime queuedAt, LocalDateTime activeUntil) {
        this.eventId = eventId;
        this.memberId = memberId;
        this.status = status;
        this.queuedAt = queuedAt;
        this.activeUntil = activeUntil;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static ReservationQueueEntry waiting(Long eventId, Long memberId) {
        return new ReservationQueueEntry(
                eventId,
                memberId,
                QueueStatus.WAITING,
                LocalDateTime.now(),
                null
        );
    }

    public void activate(LocalDateTime activeUntil) {
        this.status = QueueStatus.ACTIVE;
        this.activeUntil = activeUntil;
        this.updatedAt = LocalDateTime.now();
    }

    public void reserve() {
        this.status = QueueStatus.RESERVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void leave() {
        this.status = QueueStatus.LEFT;
        this.updatedAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = QueueStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }

    public void backToWaiting() {
        this.status = QueueStatus.WAITING;
        this.activeUntil = null;
        if (this.queuedAt == null) {
            this.queuedAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }
}
