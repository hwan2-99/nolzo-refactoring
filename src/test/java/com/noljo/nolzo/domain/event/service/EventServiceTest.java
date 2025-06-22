package com.noljo.nolzo.domain.event.service;

import com.noljo.nolzo.event.dto.EventDetailResponse;
import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventResponse;
import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.event.dto.internal.ScheduleInfo;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.event.service.EventService;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

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
    void 카테고리별_이벤트를_조회할_수_있다() {
        EventRequest concertEvent = EventFixture.캣츠dto();
        EventRequest hamletEvent = EventFixture.햄릿dto();
        eventService.save(concertEvent);
        eventService.save(hamletEvent);

        List<EventResponse> concertEvents = eventService.findAllByCategory(EventCategory.CONCERT);

        Assertions.assertThat(concertEvents).hasSize(2);
        Assertions.assertThat(concertEvents)
                .extracting("eventCategory")
                .containsOnly(EventCategory.CONCERT);
    }

    @Test
    void 존재하지_않는_카테고리의_이벤트_조회시_빈_리스트를_반환한다() {
        EventRequest concertEvent = EventFixture.캣츠dto();  // CONCERT 카테고리
        eventService.save(concertEvent);

        List<EventResponse> otherEvents = eventService.findAllByCategory(EventCategory.MUSICAL);

        Assertions.assertThat(otherEvents).isEmpty();
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
        EventUpdateRequest dtoCats2 = EventFixture.캣츠2dto();
        EventResponse response = eventService.save(dtoCats);
        Long id = response.getId();

        Assertions.assertThat("Cats").isEqualTo(eventService.findById(id).getTitle());
        EventResponse updatedResponse = eventService.update(id,dtoCats2);

        Assertions.assertThat(id).isEqualTo(updatedResponse.getId());
        Assertions.assertThat("Cats2").isEqualTo(eventService.findById(id).getTitle());
    }

    @Test
    void 이벤트_중복없이_title기반_찾기(){
        EventRequest dtoHam = EventFixture.햄릿dto();
        EventResponse response1 = eventService.save(dtoHam);
        EventRequest dtoHam2 = EventFixture.햄릿2dto();
        EventResponse response2 = eventService.save(dtoHam2);

        List<EventResponse> responses = eventService.findDistinctEventByCategory(EventCategory.CONCERT);

        Assertions.assertThat(response1.getTitle()).isEqualTo(response2.getTitle());
        Assertions.assertThat(responses.size()).isEqualTo(1);
    }

    @Test
    void 동일_이벤트_회차조회(){
        EventRequest dtoHam = EventFixture.햄릿dto();
        EventResponse response2 = eventService.save(dtoHam);
        EventRequest dtoHam2 = EventFixture.햄릿2dto();
        EventResponse response3 = eventService.save(dtoHam2);

        EventDetailResponse responseDetail = eventService.findEventDetail(response3.getId());
        System.out.println(responseDetail.getTitle());
        for(ScheduleInfo dto: responseDetail.getSchedules()){
            System.out.println(dto.getSchedule().getShowDate());
        }
    }
}