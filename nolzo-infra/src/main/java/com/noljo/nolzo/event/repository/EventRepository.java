package com.noljo.nolzo.event.repository;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findTop10ByEventCategoryOrderByViewCountDesc(EventCategory eventCategory);

    Slice<Event> findAllByEventCategory(EventCategory eventCategory, Pageable pageable);

    List<Event> findTop6ByOrderByViewCountDesc();

    List<Event> findByTitleContaining(String search);

    Slice<Event> findByEventCategoryAndAgeLimitLessThanEqual(EventCategory category, Integer age, Pageable pageable);
}
