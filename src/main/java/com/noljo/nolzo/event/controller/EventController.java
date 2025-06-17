package com.noljo.nolzo.event.controller;

import com.noljo.nolzo.event.dto.EventRequestDto;
import com.noljo.nolzo.event.dto.EventResponseDto;
import com.noljo.nolzo.event.entity.Event;
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
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.findAll());
    }



    @PostMapping("/create")
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody @Valid EventRequestDto dto) {
        return ResponseEntity.ok(eventService.save(dto));
    }

}
