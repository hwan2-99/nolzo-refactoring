package com.noljo.nolzo.event.service;

import com.noljo.nolzo.event.dto.EventRequestDto;
import com.noljo.nolzo.event.dto.EventResponseDto;
import com.noljo.nolzo.event.entity.Event;
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

    @Transactional(readOnly = true)
    public List<EventResponseDto> findAll() {
        return eventRepository.findAll().stream()
                .map(EventResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponseDto findById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트가 존재하지 않습니다 id: " + id));

        return EventResponseDto.from(event);
    }


    public EventResponseDto save(EventRequestDto dto) {
        Event saved = eventRepository.save(dto.toEntity());
        return EventResponseDto.from(saved);
    }

    public EventResponseDto update(Long id, EventRequestDto dto) {
        if (!eventRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 이벤트가 존재하지 않습니다 id: " + id);
        }

        Event updated = dto.toEntity(id);
        Event saved = eventRepository.save(updated);
        return EventResponseDto.from(saved);
    }

    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 이벤트가 없습니다 id: " + id);
        }
        eventRepository.deleteById(id);
    }
}