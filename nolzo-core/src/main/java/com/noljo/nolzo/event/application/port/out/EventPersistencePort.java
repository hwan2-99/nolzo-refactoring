package com.noljo.nolzo.event.application.port.out;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface EventPersistencePort {

    Optional<Event> findById(Long id);

    List<Event> findAll();

    <S extends Event> S save(S event);

    void deleteById(Long id);

    List<Event> findTop10ByEventCategoryOrderByViewCountDesc(EventCategory eventCategory);

    Slice<Event> findAllByEventCategory(EventCategory eventCategory, Pageable pageable);

    List<Event> findTop6ByOrderByViewCountDesc();

    List<Event> findByTitleContaining(String search);

    default Event getOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이벤트입니다. (ID: " + id + ")"));
    }

    Slice<Event> findByEventCategoryAndAgeLimitLessThanEqual(EventCategory category, Integer age, Pageable pageable);
}
