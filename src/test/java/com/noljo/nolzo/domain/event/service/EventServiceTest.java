package com.noljo.nolzo.domain.event.service;

import com.noljo.nolzo.event.dto.EventRequestDto;
import com.noljo.nolzo.event.dto.EventResponseDto;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.service.EventService;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.sun.jdi.request.EventRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@ServiceTest
class EventServiceTest {

    @Autowired
    EventService eventService;

    @Test
    void 이벤트를_저장할_수_있다() {
        EventRequestDto dto = EventFixture.캣츠dto();
        EventResponseDto response = eventService.save(dto);
        Assertions.assertThat(response.getId()).isNotNull();
        Assertions.assertThat(response.getTitle()).isEqualTo("Cats");
    }

    @Test
    void 이벤트를_조회할_수_있다(){
        EventRequestDto dto = EventFixture.캣츠dto();
        EventResponseDto response = eventService.save(dto);
        Assertions.assertThat(response.getId()).isEqualTo(eventService.findAll().get(0).getId());
    }
}