package com.noljo.nolzo.domain.event.service;

import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventResponse;
import com.noljo.nolzo.event.service.EventService;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
class EventServiceTest {

    @Autowired
    EventService eventService;

    @Test
    void 이벤트를_저장할_수_있다() {
        EventRequest dto = EventFixture.캣츠dto();
        EventResponse response = eventService.save(dto);
        Assertions.assertThat(response.getId()).isNotNull();
        Assertions.assertThat(response.getTitle()).isEqualTo("Cats");
    }

    @Test
    void 이벤트를_조회할_수_있다(){
        EventRequest dto = EventFixture.캣츠dto();
        EventResponse response = eventService.save(dto);
        Assertions.assertThat(response.getId()).isEqualTo(eventService.findAll().get(0).getId());
    }

    @Test
    void 개별_이벤트를_조회할_수_있다(){

        EventRequest dto = EventFixture.캣츠dto();

        EventResponse response = eventService.save(dto);
        Long id = response.getId();

        Assertions.assertThat("Cats").isEqualTo(eventService.findById(id).getTitle());
    }

    @Test
    void 이벤트를_삭제할_수_있다(){
        EventRequest dto = EventFixture.캣츠dto();
        EventResponse response = eventService.save(dto);
        Long id = response.getId();

        eventService.delete(id);

        Assertions.assertThatThrownBy(()->eventService.findById(id)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이벤트를_갱신할_수_있다(){
        EventRequest dtoCats = EventFixture.캣츠dto();
        EventRequest dtoHam = EventFixture.햄릿dto();
        EventResponse response = eventService.save(dtoCats);
        Long id = response.getId();

        EventResponse updatedResponse = eventService.update(id,dtoHam);

        Assertions.assertThat(id).isEqualTo(updatedResponse.getId());
        Assertions.assertThat("Hamlet").isEqualTo(updatedResponse.getTitle());
    }
}