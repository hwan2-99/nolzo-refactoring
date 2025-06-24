package com.noljo.nolzo.event.service;

import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventResponse;
import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event getEvent(Long id){
        return eventRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 이벤트가 존재하지 않습니다 id : " +id));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> findAll() {
        return eventRepository.findAll().stream()
                .map(EventResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventResponse> findAllByCategory(EventCategory category) {
        return eventRepository.findAllByEventCategory(category).stream()
                .map(EventResponse::from)
                .toList();
    }

    public EventResponse save(EventRequest dto) {
        Event saved = eventRepository.save(dto.toEntity());
        return EventResponse.from(saved);
    }

    @Transactional
    public EventResponse findById(Long id) {
        Event event = getEvent(id);
        event.addViewCount();
        return EventResponse.from(event);
    }

    public void delete(Long id) {
        getEvent(id);
        eventRepository.deleteById(id);
    }

    public List<EventResponse> searchEventList(String search) {
        List<Event> events = eventRepository.findOnePerTitle(search);
        return events.stream()
                .map(EventResponse::from)
                .toList();
    }

    public EventResponse update(Long id, EventUpdateRequest dto) {
        Event original = getEvent(id);
        original.updateFrom(dto);
        return EventResponse.from(original);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getTop10ByCategory(EventCategory category) {
        List<Event> eventList = eventRepository.findTop10ByEventCategoryOrderByViewCountDesc(category);
        return eventList.stream()
                .map(EventResponse::from)
                .toList();
    }
}