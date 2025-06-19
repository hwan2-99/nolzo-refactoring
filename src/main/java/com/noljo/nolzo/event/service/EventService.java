package com.noljo.nolzo.event.service;

import com.noljo.nolzo.event.dto.EventDetailResponse;
import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventResponse;
import com.noljo.nolzo.event.dto.internal.ScheduleInfo;
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

    @Transactional(readOnly = true)
    public EventResponse findById(Long id) {
        Event event = getEvent(id);
        return EventResponse.from(event);
    }
    public EventResponse update(Long id, EventRequest dto) {
        getEvent(id);
        Event updated = dto.toEntity(id);
        Event saved = eventRepository.save(updated);
        return EventResponse.from(saved);
    }

    public void delete(Long id) {
        getEvent(id);

        eventRepository.deleteById(id);
    }

    public List<EventResponse> findDistinctEventByCategory(EventCategory category){
        return eventRepository.findDistinctEventByCategory(category).stream()
                .map(EventResponse::from)
                .toList();
    }
    public EventDetailResponse findEventDetail(Long id){
        Event event = getEvent(id);
        List<Event> events = eventRepository.findAllByTitle(event.getTitle());
        List<ScheduleInfo> scheduleInfos = events
                .stream()
                .map(ScheduleInfo::from)
                .toList();
        return
                EventDetailResponse.from(scheduleInfos,event);
    }
//    public List<Event> findEventByTitle(String title){
//        return eventRepository
//    }
}