package com.noljo.nolzo.notification.repository;

import com.noljo.nolzo.notification.domain.NotificationDelivery;
import com.noljo.nolzo.notification.domain.NotificationDeliveryStatus;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, Long> {

    @Query("""
            select count(d) > 0
            from NotificationDelivery d
            where d.subscriptionId = :subscriptionId
              and d.status = :status
              and d.sentAt = :sentAt
            """)
    boolean existsSentHistory(
            @Param("subscriptionId") Long subscriptionId,
            @Param("status") NotificationDeliveryStatus status,
            @Param("sentAt") LocalDateTime sentAt
    );
}
