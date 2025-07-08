package com.noljo.nolzo.event.service;

import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventResponse;
import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.global.upload.S3Uploader;
import com.noljo.nolzo.schedule.dto.internal.ScheduleInfo;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.seat.service.SeatService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SeatService seatService;
    private final S3Uploader s3Uploader;
    private final EntityManager em;

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
    public List<EventResponse> findAllByCategory(EventCategory eventCategory) {
        return eventRepository.findAllByEventCategory(eventCategory, Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(EventResponse::from)
                .toList();
    }

    public EventResponse save(EventRequest dto, MultipartFile image) {
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = s3Uploader.upload(image, "event-images");
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
        List<Event> events = eventRepository.findByTitleContaining(search);
        return events.stream()
                .map(EventResponse::from)
                .toList();
    }

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
