package com.noljo.nolzo.event.controller;

import com.noljo.nolzo.auth.security.CustomUserDetails;
import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventResponse;
import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.event.application.port.in.EventUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    private final EventUseCase eventService;

    @GetMapping(params = "!category")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.findAll());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventResponse> createEvent(@RequestPart("dto") @Valid EventRequest dto,
                                                     @RequestPart(value = "eventImage", required = false) MultipartFile eventImage) {
        return ResponseEntity.ok(eventService.save(dto, eventImage));
    }

    @GetMapping
    public Slice<EventResponse> getEventsByCategory(
            @RequestParam EventCategory category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "viewCount", required = false) String condition,
            @RequestParam(required = false) Integer age
    ) {
        return eventService.getEventByCategory(category, condition, page, age);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventDetail(@PathVariable Long id) {

        return ResponseEntity.ok(eventService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventResponse>> getSearchEventList(@RequestParam(name = "search") String search) {
        return ResponseEntity.ok(eventService.searchEventList(search));
    }

    @GetMapping("/rankings")
    public ResponseEntity<List<EventResponse>> getRankingsByCategory(@RequestParam EventCategory category) {
        return ResponseEntity.ok(eventService.getTop10ByCategory(category));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<EventResponse>> getTop6PopularEvents() {
        return ResponseEntity.ok(eventService.getTop6PopularEvents());
    }

    @PatchMapping(value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @RequestPart("dto") @Valid EventUpdateRequest dto,
            @RequestPart(value = "eventImage", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EventResponse> updateJson(
            @PathVariable Long id,
            @RequestBody @Valid EventUpdateRequest dto
    ) {
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
