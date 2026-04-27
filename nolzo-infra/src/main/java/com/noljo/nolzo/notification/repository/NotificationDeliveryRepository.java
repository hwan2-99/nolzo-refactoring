package com.noljo.nolzo.notification.repository;

import com.noljo.nolzo.notification.domain.NotificationDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, Long> {
}
