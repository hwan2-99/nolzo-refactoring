package com.noljo.nolzo.event.controller;

import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventResponse;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.findAll());
    }

    @PostMapping("/")
    public ResponseEntity<EventResponse> createEvent(@RequestBody @Valid EventRequest dto) {
        return ResponseEntity.ok(eventService.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getEventsByCategory(@RequestParam EventCategory category) {
        return ResponseEntity.ok(eventService.findAllByCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @RequestBody @Valid EventRequest dto) {
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
    //    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
//        eventService.delete(id);
//        return ResponseEntity.noContent().build();
//    }



}
