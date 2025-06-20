package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import java.time.LocalDate;
import java.time.LocalTime;

import com.noljo.nolzo.event.entity.Schedule;
import lombok.Getter;

@Getter
public enum EventFixture {
    캣츠("Cats", "서울 예술의 전당", "세계적으로 유명한 뮤지컬, 고양이들의 이야기를 그린 작품입니다.",
            "https://example.com/cats.jpg", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 6, 30),
            new Schedule(LocalDate.of(2024, 5, 10), LocalTime.of(19, 30)), EventCategory.CONCERT, 180, 12, 5, 120),
    햄릿("Hamlet", "국립극장 해오름극장", "셰익스피어 4대 비극 중 하나인 '햄릿'의 현대적 재해석 공연입니다.",
            "https://example.com/hamlet.jpg", LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 15),
            new Schedule(LocalDate.of(2024, 7, 10), LocalTime.of(18, 30)), EventCategory.CONCERT, 150, 15, 4, 80),
    햄릿일정("Hamlet", "국립극장 해오름극장", "셰익스피어 4대 비극 중 하나인 '햄릿'의 현대적 재해석 공연.",
               "https://example.com/hamlet.jpg", LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 15),
            new Schedule(LocalDate.of(2024, 5, 10), LocalTime.of(19, 30)), EventCategory.CONCERT, 150, 15, 4, 80);

    private String title;
    private String venue;
    private String description;
    private String posterImageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private Schedule schedule;
    private EventCategory eventCategory;
    private int runtime;
    private int ageLimit;
    private int rating;
    private int reviewCount;


    EventFixture(String title, String venue, String description, String posterImageUrl,
                 LocalDate startDate, LocalDate endDate, Schedule schedule,
                 EventCategory eventCategory, int runtime, int ageLimit, int rating, int reviewCount
    ) {
        this.title = title;
        this.venue = venue;
        this.description = description;
        this.posterImageUrl = posterImageUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.schedule = schedule;
        this.eventCategory = eventCategory;
        this.runtime = runtime;
        this.ageLimit = ageLimit;
        this.rating = rating;
        this.reviewCount = reviewCount;
    }

    public static Event 캣츠() {
        return new Event(null, 캣츠.title, 캣츠.venue, 캣츠.description, 캣츠.posterImageUrl,
                캣츠.startDate, 캣츠.endDate, 캣츠.schedule, 캣츠.eventCategory, 캣츠.runtime, 캣츠.ageLimit, 캣츠.rating, 캣츠.reviewCount);
    }

    public static Event 햄릿() {
        return new Event(null, 햄릿.title, 햄릿.venue, 햄릿.description, 햄릿.posterImageUrl,
                햄릿.startDate, 햄릿.endDate, 햄릿.schedule, 햄릿.eventCategory, 햄릿.runtime, 햄릿.ageLimit, 햄릿.rating, 햄릿.reviewCount);
    }

    public static EventRequest 캣츠dto() {
        return EventRequest.builder()
                .title(캣츠.title)
                .venue(캣츠.venue)
                .description(캣츠.description)
                .posterImageUrl(캣츠.posterImageUrl)
                .startDate(캣츠.startDate)
                .endDate(캣츠.endDate)
                .schedule(캣츠.schedule)
                .eventCategory(캣츠.eventCategory)
                .runtime(캣츠.runtime)
                .ageLimit(캣츠.ageLimit)
                .build();
    }

    public static EventRequest 햄릿dto() {
        return EventRequest.builder()
                .title(햄릿.title)
                .venue(햄릿.venue)
                .description(햄릿.description)
                .posterImageUrl(햄릿.posterImageUrl)
                .startDate(햄릿.startDate)
                .endDate(햄릿.endDate)
                .schedule(햄릿.schedule)
                .eventCategory(햄릿.eventCategory)
                .runtime(햄릿.runtime)
                .ageLimit(햄릿.ageLimit)
                .build();
    }
    public static EventRequest 햄릿2dto() {
        return EventRequest.builder()
                .title(햄릿일정.title)
                .venue(햄릿일정.venue)
                .description(햄릿일정.description)
                .posterImageUrl(햄릿일정.posterImageUrl)
                .startDate(햄릿일정.startDate)
                .endDate(햄릿일정.endDate)
                .schedule(햄릿일정.schedule)
                .eventCategory(햄릿일정.eventCategory)
                .runtime(햄릿일정.runtime)
                .ageLimit(햄릿일정.ageLimit)
                .build();
    }
}