package com.noljo.nolzo.event.repository;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByEventCategory(EventCategory eventCategory);

    List<Event> findTop10ByEventCategoryOrderByViewCountDesc(EventCategory eventCategory);

    List<Event> findByTitleContaining(String search);
}
