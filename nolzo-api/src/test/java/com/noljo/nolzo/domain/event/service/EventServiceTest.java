package com.noljo.nolzo.domain.event.service;

import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventResponse;
import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.event.application.port.out.EventPersistencePort;
import com.noljo.nolzo.event.service.EventService;
import com.noljo.nolzo.schedule.dto.ScheduleResponse;
import com.noljo.nolzo.support.annotation.ServiceTest;
import com.noljo.nolzo.support.fixture.EventFixture;
import com.noljo.nolzo.support.fixture.FileFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;

@ServiceTest
class EventServiceTest {

    @Autowired
    EventService eventService;

    @Autowired
    EventPersistencePort eventRepository;

    MultipartFile image = FileFixture.dummyImage();

    @Test
    void 이벤트를_저장할_수_있다() {
        EventRequest dto = EventFixture.캣츠dto();
        EventResponse response = eventService.save(dto, null);
        Assertions.assertThat(response.getId()).isNotNull();
        Assertions.assertThat(response.getTitle()).isEqualTo("Cats");
    }

    @Test
    void 이벤트를_조회할_수_있다() {
        EventRequest dto = EventFixture.캣츠dto();
        EventResponse response = eventService.save(dto, null);
        Assertions.assertThat(response.getId()).isEqualTo(eventService.findAll().get(0).getId());
    }

    @Test
    void 개별_이벤트를_조회할_수_있다() {
        EventRequest dto = EventFixture.캣츠dto();

        EventResponse response = eventService.save(dto, null);
        Long id = response.getId();

        Assertions.assertThat("Cats").isEqualTo(eventService.findById(id).getTitle());
    }

    @Test
    void 제목에_검색어가_포함된_이벤트만_조회된다() {
        Event event1 = eventRepository.save(EventFixture.셜록_블러디());
        Event event2 = eventRepository.save(EventFixture.셜록_앤더슨());

        List<EventResponse> result = eventService.searchEventList("셜록");

        Assertions.assertThat(result)
                .hasSize(2)
                .allSatisfy(event ->
                        Assertions.assertThat(event.getTitle()).contains("셜록"));
    }

    @Test
    void 카테고리별_페이징_처리_및_이벤트_조회_테스트() {
        EventRequest dto2 = EventFixture.셜록_블러디_dto();
        EventResponse response2 = eventService.save(dto2, null);

        EventRequest dto3 = EventFixture.셜록_앤더슨_dto();
        EventResponse response3 = eventService.save(dto3, null);

        Slice<EventResponse> result = eventService.getEventByCategory(EventCategory.MUSICAL, "ranking",0,20);

        Assertions.assertThat(result.getContent())
                .hasSize(2)
                .allSatisfy(event -> Assertions.assertThat(event.getEventCategory()).isEqualTo(EventCategory.MUSICAL));

        Assertions.assertThat(result.hasNext()).isFalse();
    }

    @Test
    void 존재하지_않는_카테고리의_이벤트_조회시_빈_리스트를_반환한다() {
        EventRequest concertEvent = EventFixture.캣츠dto();
        eventService.save(concertEvent, null);

        Slice<EventResponse> otherEvents = eventService.getEventByCategory(EventCategory.MUSICAL, "null",0,0);

        Assertions.assertThat(otherEvents.getContent()).isEmpty();
    }

    @Test
    void 이벤트를_삭제할_수_있다() {
        EventRequest dto = EventFixture.캣츠dto();
        EventResponse response = eventService.save(dto, null);
        Long id = response.getId();

        eventService.delete(id);

        Assertions.assertThatThrownBy(() -> eventService.findById(id)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이벤트와_스케줄_정보를_갱신할_수_있다() {
        EventRequest originalRequest = EventFixture.캣츠dto();
        EventUpdateRequest updateRequest = EventFixture.캣츠2dto();

        EventResponse savedResponse = eventService.save(originalRequest, null);
        Long id = savedResponse.getId();

        EventResponse updatedResponse = eventService.update(id, updateRequest);

        Assertions.assertThat(updatedResponse.getTitle()).isEqualTo(updateRequest.getTitle());
        Assertions.assertThat(updatedResponse.getVenue()).isEqualTo(updateRequest.getVenue());
        Assertions.assertThat(updatedResponse.getDescription()).isEqualTo(updateRequest.getDescription());

        List<ScheduleResponse> updatedSchedules = eventService.findById(id).getSchedules();

        Assertions.assertThat(updatedSchedules)
                .hasSameSizeAs(updateRequest.getSchedules());
        for (int i = 0; i < updatedSchedules.size(); i++) {
            Assertions.assertThat(updatedSchedules.get(i).getShowDate())
                    .isEqualTo(updateRequest.getSchedules().get(i).getShowDate());
            Assertions.assertThat(updatedSchedules.get(i).getShowTime())
                    .isEqualTo(updateRequest.getSchedules().get(i).getShowTime());
        }
    }

    @Test
    void 조회하면_viewCount가_1_증가한다() {
        Event event = Event.builder()
                .title("뮤지컬 캣츠")
                .venue("세종문화회관")
                .description("전설의 고양이")
                .posterImageUrl(null)
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2025, 7, 10))
                .eventCategory(EventCategory.MUSICAL)
                .runtime(120)
                .ageLimit(12)
                .build();

        event = eventRepository.save(event);

        eventService.findById(event.getId());
        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();

        Assertions.assertThat(updatedEvent.getViewCount()).isEqualTo(1);
    }

    @Test
    void 카테고리별로_상위_10개의_이벤트를_조회할_수_있다() {
        Event cats = eventRepository.save(EventFixture.캣츠());
        Event hamlet = eventRepository.save(EventFixture.햄릿());

        for (int i = 0; i < 5; i++) {
            cats.addViewCount();
        }
        for (int i = 0; i < 10; i++) {
            hamlet.addViewCount();
        }

        eventRepository.save(cats);
        eventRepository.save(hamlet);

        List<EventResponse> result = eventService.getTop10ByCategory(EventCategory.CONCERT);

        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(hamlet.getId());
        Assertions.assertThat(result.get(1).getId()).isEqualTo(cats.getId());
        Assertions.assertThat(result.get(0).getViewCount()).isGreaterThan(result.get(1).getViewCount());
    }
}
