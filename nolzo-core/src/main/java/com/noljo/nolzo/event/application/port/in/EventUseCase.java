package com.noljo.nolzo.event.application.port.in;

import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventResponse;
import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.event.entity.EventCategory;
import java.util.List;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

public interface EventUseCase {

    List<EventResponse> findAll();

    Slice<EventResponse> getEventByCategory(EventCategory eventCategory, String condition, int page, Integer age);

    EventResponse save(EventRequest dto, MultipartFile image);

    EventResponse findById(Long eventId);

    void delete(Long id);

    List<EventResponse> searchEventList(String search);

    EventResponse update(Long id, EventUpdateRequest dto);

    List<EventResponse> getTop10ByCategory(EventCategory category);

    List<EventResponse> getTop6PopularEvents();
}
