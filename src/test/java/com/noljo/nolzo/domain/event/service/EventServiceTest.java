package com.noljo.nolzo.domain.event.service;

import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventResponse;
import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.event.repository.EventRepository;
import com.noljo.nolzo.event.service.EventService;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.FileFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@ServiceTest
class EventServiceTest {

    @Autowired
    EventService eventService;

    @Autowired
    EventRepository eventRepository;

    MultipartFile image = FileFixture.dummyImage();

    @Test
    void 이벤트를_저장할_수_있다() {
        EventRequest dto = EventFixture.캣츠dto();
        EventResponse response = eventService.save(dto,image);
        Assertions.assertThat(response.getId()).isNotNull();
        Assertions.assertThat(response.getTitle()).isEqualTo("Cats");
    }

    @Test
    void 이벤트를_조회할_수_있다() {
        EventRequest dto = EventFixture.캣츠dto();
        EventResponse response = eventService.save(dto,image);
        Assertions.assertThat(response.getId()).isEqualTo(eventService.findAll().get(0).getId());
    }

    @Test
    void 개별_이벤트를_조회할_수_있다() {

        EventRequest dto = EventFixture.캣츠dto();

        EventResponse response = eventService.save(dto,image);
        Long id = response.getId();

        Assertions.assertThat("Cats").isEqualTo(eventService.findById(id).getTitle());
    }

    @Test
    void 카테고리별_이벤트를_조회할_수_있다() {
        EventRequest concertEvent = EventFixture.캣츠dto();
        EventRequest hamletEvent = EventFixture.햄릿dto();
        eventService.save(concertEvent,image);
        eventService.save(hamletEvent,image);

        List<EventResponse> concertEvents = eventService.findAllByCategory(EventCategory.CONCERT);

        Assertions.assertThat(concertEvents).hasSize(2);
        Assertions.assertThat(concertEvents)
                .extracting("eventCategory")
                .containsOnly(EventCategory.CONCERT);
    }

    @Test
    void 존재하지_않는_카테고리의_이벤트_조회시_빈_리스트를_반환한다() {
        EventRequest concertEvent = EventFixture.캣츠dto();  // CONCERT 카테고리
        eventService.save(concertEvent,image);

        List<EventResponse> otherEvents = eventService.findAllByCategory(EventCategory.MUSICAL);

        Assertions.assertThat(otherEvents).isEmpty();
    }


    @Test
    void 이벤트를_삭제할_수_있다() {
        EventRequest dto = EventFixture.캣츠dto();
        EventResponse response = eventService.save(dto,image);
        Long id = response.getId();

        eventService.delete(id);

        Assertions.assertThatThrownBy(() -> eventService.findById(id)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이벤트를_갱신할_수_있다() {
        EventRequest dtoCats = EventFixture.캣츠dto();
        EventUpdateRequest dtoCats2 = EventFixture.캣츠2dto();
        EventResponse response = eventService.save(dtoCats,image);
        Long id = response.getId();

        Assertions.assertThat("Cats").isEqualTo(eventService.findById(id).getTitle());
        EventResponse updatedResponse = eventService.update(id, dtoCats2);

        Assertions.assertThat(id).isEqualTo(updatedResponse.getId());
        Assertions.assertThat("Cats2").isEqualTo(eventService.findById(id).getTitle());
    }

    @Test
    void 조회하면_viewCount가_1_증가한다() {
        Event event = Event.builder()
                .title("뮤지컬 캣츠")
                .venue("세종문화회관")
                .description("전설의 고양이")
                .posterImageUrl("http://image.url")
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2025, 7, 10))
                .eventCategory(EventCategory.MUSICAL)
                .runtime(120)
                .ageLimit(12)
                .rating(5)
                .reviewCount(10)
                .build();

        event = eventRepository.save(event);

        eventService.findById(event.getId());
        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();

        Assertions.assertThat(updatedEvent.getViewCount()).isEqualTo(1);
    }

    @Test
    void 카테고리별로_상위_10개의_이벤트를_조회할_수_있다() {
        Event cats   = eventRepository.save(EventFixture.캣츠());
        Event hamlet = eventRepository.save(EventFixture.햄릿());

        for (int i = 0; i < 5; i++)  cats.addViewCount();
        for (int i = 0; i < 10; i++) hamlet.addViewCount();

        eventRepository.save(cats);
        eventRepository.save(hamlet);

        List<EventResponse> result =
                eventService.getTop10ByCategory(EventCategory.CONCERT);

        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(hamlet.getId());
        Assertions.assertThat(result.get(1).getId()).isEqualTo(cats.getId());
        Assertions.assertThat(result.get(0).getViewCount()).isGreaterThan(result.get(1).getViewCount());
    }
}