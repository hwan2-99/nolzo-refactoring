package com.noljo.nolzo.event.service;

import com.noljo.nolzo.event.application.port.out.EventImageUploadPort;
import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventResponse;
import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.seat.service.SeatService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private static final int SIZE = 12;
    private static final String SORT_BY_DATE = "createdAt";
    private final EventRepository eventRepository;
    private final SeatService seatService;
    private final EventImageUploadPort eventImageUploadPort;
    private final EntityManager em;

    @Transactional(readOnly = true)
    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트가 존재하지 않습니다 id : " + id));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> findAll() {
        return eventRepository.findAll().stream()
                .map(EventResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<EventResponse> getEventByCategory(EventCategory eventCategory, String condition, int page, Integer age) {
        Pageable pageable = PageRequest.of(page, SIZE, getCondition(condition));
        if (age != null) {
            return eventRepository.findByEventCategoryAndAgeLimitLessThanEqual(eventCategory, age, pageable)
                    .map(EventResponse::from);
        }
        return eventRepository.findAllByEventCategory(eventCategory, pageable)
                .map(EventResponse::from);
    }

    private Sort getCondition(String condition) {
        return switch (condition) {
            case "reviewCount" -> Sort.by(Sort.Direction.DESC, "viewCount");
            case "reviewStar"  -> Sort.by(Sort.Direction.DESC, "ratingAvg");
            case "ranking"     -> Sort.by(Sort.Direction.DESC, "viewCount");
            default            -> Sort.by(Sort.Direction.DESC, SORT_BY_DATE);
        };
    }

    @Transactional
    public EventResponse save(EventRequest dto, MultipartFile image) {
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = eventImageUploadPort.upload(image, "event-images");
            } catch (IOException e) {
                throw new RuntimeException("S3 이미지 업로드 실패: " + e.getMessage(), e);
            }
        }

        Event event = dto.toEntity(imageUrl);
        Event saved = eventRepository.save(event);

        saved.getSchedules().forEach(schedule -> seatService.createSeats(schedule.getId()));

        return EventResponse.from(saved);
    }

    @Transactional
    public EventResponse findById(Long eventId) {
        Event event = getEvent(eventId);
        event.addViewCount();
        return EventResponse.from(event);
    }

    @Transactional
    public void delete(Long id) {
        getEvent(id);
        eventRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> searchEventList(String search) {
        List<Event> events = eventRepository.findByTitleContaining(search);
        return events.stream()
                .map(EventResponse::from)
                .toList();
    }

    @Transactional
    public EventResponse update(Long id, EventUpdateRequest dto) {
        Event original = getEvent(id);
        Set<Long> originalSchedules = original.getSchedules().stream()
                .map(Schedule::getId)
                .collect(Collectors.toSet());

        original.updateFrom(dto);

        em.flush();

        original.getSchedules().stream()
                .filter(schedule -> !originalSchedules.contains(schedule.getId()))
                .forEach(schedule -> seatService.createSeats(schedule.getId()));

        return EventResponse.from(original);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getTop10ByCategory(EventCategory category) {
        List<Event> eventList = eventRepository.findTop10ByEventCategoryOrderByViewCountDesc(category);
        return eventList.stream()
                .map(EventResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getTop6PopularEvents() {
        List<Event> popularEvents = eventRepository.findTop6ByOrderByViewCountDesc();
        return popularEvents.stream()
                .map(EventResponse::from)
                .toList();
    }
}
