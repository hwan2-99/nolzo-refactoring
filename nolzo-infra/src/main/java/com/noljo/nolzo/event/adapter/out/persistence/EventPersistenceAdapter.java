package com.noljo.nolzo.event.adapter.out.persistence;

import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.event.repository.EventRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPersistenceAdapter implements EventPersistencePort {

    private final EventRepository eventRepository;

    @Override
    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public <S extends Event> S save(S event) {
        return eventRepository.save(event);
    }

    @Override
    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public List<Event> findTop10ByEventCategoryOrderByViewCountDesc(EventCategory eventCategory) {
        return eventRepository.findTop10ByEventCategoryOrderByViewCountDesc(eventCategory);
    }

    @Override
    public Slice<Event> findAllByEventCategory(EventCategory eventCategory, Pageable pageable) {
        return eventRepository.findAllByEventCategory(eventCategory, pageable);
    }

    @Override
    public List<Event> findTop6ByOrderByViewCountDesc() {
        return eventRepository.findTop6ByOrderByViewCountDesc();
    }

    @Override
    public List<Event> findByTitleContaining(String search) {
        return eventRepository.findByTitleContaining(search);
    }

    @Override
    public Slice<Event> findByEventCategoryAndAgeLimitLessThanEqual(EventCategory category, Integer age,
                                                                    Pageable pageable) {
        return eventRepository.findByEventCategoryAndAgeLimitLessThanEqual(category, age, pageable);
    }
}
