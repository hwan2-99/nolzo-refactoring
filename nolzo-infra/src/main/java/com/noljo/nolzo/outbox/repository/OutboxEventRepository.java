package com.noljo.nolzo.outbox.repository;

import com.noljo.nolzo.outbox.domain.OutboxEvent;
import com.noljo.nolzo.outbox.domain.OutboxEventStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByStatusInOrderByIdAsc(List<OutboxEventStatus> statuses, Pageable pageable);
}
