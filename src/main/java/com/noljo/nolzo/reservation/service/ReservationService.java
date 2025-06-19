package com.noljo.nolzo.reservation.service;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.reservation.dto.EventDateTimeResponse;
import com.noljo.nolzo.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;

    public EventDateTimeResponse chooseEventDateTime(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트가 존재하지 않습니다"));
        return EventDateTimeResponse.fromEvent(event);
    }



}
