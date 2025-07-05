package com.noljo.nolzo.event.repository;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findTop10ByEventCategoryOrderByViewCountDesc(EventCategory eventCategory);

    List<Event> findAllByEventCategoryOrderByCreatedDateDesc(EventCategory eventCategory);

    List<Event> findTop6ByOrderByViewCountDesc();
  
    List<Event> findByTitleContaining(String search);

    default Event getOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다. (ID: " + id + ")"));
    }
}
