package com.noljo.nolzo.event.service;

import com.noljo.nolzo.event.dto.EventRequestDto;
import com.noljo.nolzo.event.dto.EventResponseDto;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public EventResponseDto save(EventRequestDto dto) {
        Event saved = eventRepository.save(dto.toEntity());
        return EventResponseDto.from(saved);
    }

    public List<EventResponseDto> findAll() {
        return eventRepository.findAll().stream()
                .map(EventResponseDto::from)
                .toList();
    }

}