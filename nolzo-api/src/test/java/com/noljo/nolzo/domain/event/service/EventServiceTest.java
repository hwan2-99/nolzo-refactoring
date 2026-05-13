package com.noljo.nolzo.domain.event.service;

import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventRecommendRequest;
import com.noljo.nolzo.event.dto.EventRecommendResponse;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@ServiceTest
class EventServiceTest {

    @Autowired
    EventService eventService;

    @Autowired
    EventPersistencePort eventPersistencePort;

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
        Event event1 = eventPersistencePort.save(EventFixture.셜록_블러디());
        Event event2 = eventPersistencePort.save(EventFixture.셜록_앤더슨());

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

        event = eventPersistencePort.save(event);

        eventService.findById(event.getId());
        Event updatedEvent = eventPersistencePort.findById(event.getId()).orElseThrow();

        Assertions.assertThat(updatedEvent.getViewCount()).isEqualTo(1);
    }

    @Test
    void 카테고리별로_상위_10개의_이벤트를_조회할_수_있다() {
        Event cats = eventPersistencePort.save(EventFixture.캣츠());
        Event hamlet = eventPersistencePort.save(EventFixture.햄릿());

        for (int i = 0; i < 5; i++) {
            cats.addViewCount();
        }
        for (int i = 0; i < 10; i++) {
            hamlet.addViewCount();
        }

        eventPersistencePort.save(cats);
        eventPersistencePort.save(hamlet);

        List<EventResponse> result = eventService.getTop10ByCategory(EventCategory.CONCERT);

        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(hamlet.getId());
        Assertions.assertThat(result.get(1).getId()).isEqualTo(cats.getId());
        Assertions.assertThat(result.get(0).getViewCount()).isGreaterThan(result.get(1).getViewCount());
    }

    @Test
    void 추천_질의에서_지역과_카테고리를_해석해_조건에_맞는_공연을_추천한다() {
        LocalDate weekendStart = LocalDate.now().with(java.time.DayOfWeek.SATURDAY);
        eventService.save(추천용_이벤트_요청(
                "서울 주말 뮤지컬",
                "서울 공연장",
                "데이트하기 좋은 뮤지컬 공연",
                weekendStart,
                weekendStart.plusDays(1),
                EventCategory.MUSICAL
        ), null);
        eventService.save(추천용_이벤트_요청(
                "부산 콘서트",
                "부산 공연장",
                "신나는 콘서트 공연",
                weekendStart,
                weekendStart.plusDays(1),
                EventCategory.CONCERT
        ), null);

        EventRecommendResponse response = eventService.recommendEvents(
                new EventRecommendRequest(null, "서울에서 뮤지컬 추천해줘")
        );

        Assertions.assertThat(response.condition().region()).isEqualTo("서울");
        Assertions.assertThat(response.condition().category()).isEqualTo("MUSICAL");
        Assertions.assertThat(response.message()).isEqualTo("입력한 조건에 맞는 공연을 추천합니다.");
        Assertions.assertThat(response.recommendations())
                .isNotEmpty()
                .allSatisfy(item -> {
                    Assertions.assertThat(item.venue()).contains("서울");
                    Assertions.assertThat(item.category()).isEqualTo(EventCategory.MUSICAL);
                    Assertions.assertThat(item.recommendationReason()).contains("서울");
                });
    }

    @Test
    void 추천_질의의_가격_상한보다_최저_좌석가가_비싸면_추천하지_않는다() {
        LocalDate weekendStart = LocalDate.now().with(java.time.DayOfWeek.SATURDAY);
        eventService.save(추천용_이벤트_요청(
                "서울 가격 테스트 공연",
                "서울 테스트홀",
                "가격 조건 테스트용 공연",
                weekendStart,
                weekendStart.plusDays(1),
                EventCategory.CONCERT
        ), null);

        EventRecommendResponse response = eventService.recommendEvents(
                new EventRecommendRequest(null, "서울에서 7만원 이하 공연 추천해줘")
        );

        Assertions.assertThat(response.condition().maxPrice()).isEqualTo(70_000);
        Assertions.assertThat(response.message()).isEqualTo("입력한 조건과 정확히 일치하는 공연이 없어 인기 공연을 대신 추천합니다.");
        Assertions.assertThat(response.recommendations()).isNotEmpty();
        Assertions.assertThat(response.recommendations())
                .allSatisfy(item ->
                        Assertions.assertThat(item.recommendationReason()).contains("정확히 일치하는 공연이 없어"));
    }

    @Test
    void 이번_주말_조건이면_주말에_열리는_공연만_추천한다() {
        LocalDate saturday = LocalDate.now().with(java.time.DayOfWeek.SATURDAY);
        eventService.save(추천용_이벤트_요청(
                "주말 공연",
                "서울 아트홀",
                "이번 주말에 열리는 공연",
                saturday,
                saturday.plusDays(1),
                EventCategory.PLAY
        ), null);
        eventService.save(추천용_이벤트_요청(
                "다음 주 공연",
                "서울 아트홀",
                "다음 주에 열리는 공연",
                saturday.plusDays(3),
                saturday.plusDays(4),
                EventCategory.PLAY
        ), null);

        EventRecommendResponse response = eventService.recommendEvents(
                new EventRecommendRequest(null, "이번 주말 공연 추천해줘")
        );

        Assertions.assertThat(response.condition().dateRange()).isEqualTo("이번 주말");
        Assertions.assertThat(response.message()).isEqualTo("입력한 조건에 맞는 공연을 추천합니다.");
        Assertions.assertThat(response.recommendations())
                .extracting(item -> item.title())
                .contains("주말 공연")
                .doesNotContain("다음 주 공연");
    }

    @Test
    void 조건에_맞는_공연이_없으면_인기_공연을_fallback으로_추천한다() {
        LocalDate saturday = LocalDate.now().with(java.time.DayOfWeek.SATURDAY);
        EventResponse popularEvent = eventService.save(추천용_이벤트_요청(
                "인기 공연",
                "서울 아트홀",
                "많이 찾는 공연",
                saturday,
                saturday.plusDays(1),
                EventCategory.CONCERT
        ), null);
        eventService.findById(popularEvent.getId());
        eventService.findById(popularEvent.getId());

        EventRecommendResponse response = eventService.recommendEvents(
                new EventRecommendRequest(null, "광주에서 7만원 이하 연극 추천해줘")
        );

        Assertions.assertThat(response.message()).isEqualTo("입력한 조건과 정확히 일치하는 공연이 없어 인기 공연을 대신 추천합니다.");
        Assertions.assertThat(response.recommendations()).isNotEmpty();
        Assertions.assertThat(response.recommendations().get(0).recommendationReason())
                .contains("정확히 일치하는 공연이 없어");
    }

    private EventRequest 추천용_이벤트_요청(
            String title,
            String venue,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            EventCategory category
    ) {
        return EventRequest.builder()
                .title(title)
                .venue(venue)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .eventCategory(category)
                .runtime(120)
                .ageLimit(12)
                .schedules(List.of(
                        new com.noljo.nolzo.schedule.dto.internal.ScheduleInfo(
                                startDate,
                                LocalTime.of(19, 0),
                                LocalDateTime.now().minusDays(1),
                                LocalDateTime.now().plusDays(30)
                        )
                ))
                .build();
    }
}
