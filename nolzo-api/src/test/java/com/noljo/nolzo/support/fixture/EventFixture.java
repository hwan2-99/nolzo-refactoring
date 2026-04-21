package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.schedule.dto.internal.ScheduleInfo;
import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import com.noljo.nolzo.schedule.entity.Schedule;
import lombok.Getter;

@Getter
public enum EventFixture {
    캣츠("Cats", "서울 예술의 전당", "세계적으로 유명한 뮤지컬, 고양이들의 이야기를 그린 작품입니다.",
            "https://example.com/cats.jpg", LocalDate.of(2024, 3, 1), LocalDate.of(2026, 6, 30),
            EventCategory.CONCERT, 180, 12, 5, 120
            , LocalDateTime.of(2024, 2, 25, 12, 0), LocalDateTime.of(2026, 2, 27, 12, 0)
    ),
    캣츠2("Cats2", "서울 예술의 전당", "세계적으로 유명한 뮤지컬, 고양이들의 이야기를 그린 작품입니다.",
            "https://example.com/cats.jpg", LocalDate.of(2024, 3, 1), LocalDate.of(2026, 6, 30),
            EventCategory.CONCERT, 180, 12, 5, 120
            , LocalDateTime.of(2024, 2, 25, 12, 0), LocalDateTime.of(2026, 2, 27, 12, 0)
    ),
    햄릿("Hamlet", "국립극장 해오름극장", "셰익스피어 4대 비극 중 하나인 '햄릿'의 현대적 재해석 공연입니다.",
            "https://example.com/hamlet.jpg", LocalDate.of(2024, 7, 1), LocalDate.of(2026, 7, 15),
            EventCategory.CONCERT, 150, 15, 4, 80
            , LocalDateTime.of(2024, 2, 25, 12, 0), LocalDateTime.of(2026, 2, 27, 12, 0)
    ),
    햄릿일정("Hamlet", "국립극장 해오름극장", "셰익스피어 4대 비극 중 하나인 '햄릿'의 현대적 재해석 공연.",
            "https://example.com/hamlet.jpg", LocalDate.of(2024, 7, 1), LocalDate.of(2026, 7, 15),
            EventCategory.CONCERT, 150, 15, 4, 80
            , LocalDateTime.of(2024, 2, 25, 12, 0), LocalDateTime.of(2026, 2, 27, 12, 0)
    ),
    셜록_블러디("셜록홈즈: 블러디 게임", "LG아트센터 서울", "정체불명의 연쇄 살인 사건을 둘러싼 셜록의 추리와 심리전",
            "http://image.url/sherlock-bloody.jpg", LocalDate.of(2025, 11, 5),
            LocalDate.of(2025, 12, 20), EventCategory.MUSICAL, 155, 15, 0, 0,
            LocalDateTime.of(2025, 10, 1, 12, 0), LocalDateTime.of(2025, 10, 30, 12, 0)
    ),
    셜록_앤더슨("셜록홈즈: 앤더슨가의 비밀", "광림아트센터 BBCH홀", "셜록 홈즈와 왓슨이 앤더슨 가문의 미스터리를 파헤치는 이야기",
            "http://image.url/sherlock-anderson.jpg", LocalDate.of(2025, 10, 15),
            LocalDate.of(2025, 12, 20), EventCategory.MUSICAL, 155, 15, 0, 0,
            LocalDateTime.of(2025, 9, 1, 12, 0), LocalDateTime.of(2025, 9, 30, 12, 0)
    );

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
    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;

    EventFixture(String title, String venue, String description, String posterImageUrl,
                 LocalDate startDate, LocalDate endDate,
                 EventCategory eventCategory, int runtime, int ageLimit, int rating, int reviewCount,
                 LocalDateTime reservationStart,
                 LocalDateTime reservationEnd
    ) {
        this.title = title;
        this.venue = venue;
        this.description = description;
        this.posterImageUrl = posterImageUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventCategory = eventCategory;
        this.runtime = runtime;
        this.ageLimit = ageLimit;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.reservationStart = reservationStart;
        this.reservationEnd = reservationEnd;
    }

    public static Event 캣츠() {
        Event event = new Event(null, 캣츠.title, 캣츠.venue, 캣츠.description, 캣츠.posterImageUrl,
                캣츠.startDate, 캣츠.endDate, 캣츠.eventCategory, 캣츠.runtime, 캣츠.ageLimit);
        return event;
    }

    public static Event 햄릿() {
        Event event = new Event(null, 햄릿.title, 햄릿.venue, 햄릿.description, 햄릿.posterImageUrl,
                햄릿.startDate, 햄릿.endDate, 햄릿.eventCategory, 햄릿.runtime, 햄릿.ageLimit);
        return event;
    }

    public static Event 셜록_블러디() {
        Event event = new Event(null, 셜록_블러디.title, 셜록_블러디.venue, 셜록_블러디.description, 셜록_블러디.posterImageUrl,
                셜록_블러디.startDate, 셜록_블러디.endDate, 셜록_블러디.eventCategory, 셜록_블러디.runtime, 셜록_블러디.ageLimit);
        return event;
    }

    public static Event 셜록_앤더슨() {
        Event event = new Event(null, 셜록_앤더슨.title, 셜록_앤더슨.venue, 셜록_앤더슨.description, 셜록_앤더슨.posterImageUrl,
                셜록_앤더슨.startDate, 셜록_앤더슨.endDate, 셜록_앤더슨.eventCategory, 셜록_앤더슨.runtime, 셜록_앤더슨.ageLimit);
        return event;
    }

    public static EventRequest 캣츠dto() {
        return EventRequest.builder()
                .title(캣츠.title)
                .venue(캣츠.venue)
                .description(캣츠.description)
                .posterImageUrl(캣츠.posterImageUrl)
                .startDate(캣츠.startDate)
                .endDate(캣츠.endDate)
                .eventCategory(캣츠.eventCategory)
                .runtime(캣츠.runtime)
                .ageLimit(캣츠.ageLimit)
                .schedules(List.of(
                        new ScheduleInfo(
                                LocalDate.of(2025, 5, 10),
                                LocalTime.of(19, 30),
                                LocalDateTime.of(2023,6,25,11,0,0),
                                LocalDateTime.of(2026,6,25,11,0,0)
                        )
                ))
                .build();
    }

    public static EventUpdateRequest 캣츠2dto() {
        return EventUpdateRequest.builder()
                .title(캣츠2.title)
                .venue(캣츠2.venue)
                .description(캣츠2.description)
                .startDate(캣츠2.startDate)
                .endDate(캣츠2.endDate)
                .schedules(List.of(
                        new ScheduleInfo(
                                LocalDate.of(2025, 5, 12),
                                LocalTime.of(18, 0),
                                LocalDateTime.of(2025,6,25,11,0,0),
                                LocalDateTime.of(2026,6,25,11,0,0)
                        )
                ))
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
                .eventCategory(햄릿.eventCategory)
                .runtime(햄릿.runtime)
                .ageLimit(햄릿.ageLimit)
                .schedules(List.of(
                        new ScheduleInfo(
                                LocalDate.of(2025, 5, 10),
                                LocalTime.of(19, 30),
                                LocalDateTime.of(2023,6,25,11,0,0),
                                LocalDateTime.of(2026,6,25,11,0,0)
                        )
                ))
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
                .eventCategory(햄릿일정.eventCategory)
                .runtime(햄릿일정.runtime)
                .ageLimit(햄릿일정.ageLimit)
                .schedules(List.of(
                        new ScheduleInfo(
                                LocalDate.of(2025, 5, 10),
                                LocalTime.of(19, 30),
                                LocalDateTime.of(2023,6,25,11,0,0),
                                LocalDateTime.of(2026,6,25,11,0,0)
                        )
                ))
                .build();
    }

    public static EventRequest 셜록_블러디_dto() {
        return EventRequest.builder()
                .title(셜록_블러디.title)
                .venue(셜록_블러디.venue)
                .description(셜록_블러디.description)
                .posterImageUrl(셜록_블러디.posterImageUrl)
                .startDate(셜록_블러디.startDate)
                .endDate(셜록_블러디.endDate)
                .eventCategory(셜록_블러디.eventCategory)
                .runtime(셜록_블러디.runtime)
                .ageLimit(셜록_블러디.ageLimit)
                .schedules(List.of(
                        new ScheduleInfo(
                                LocalDate.of(2025, 11, 6),
                                LocalTime.of(19, 0),
                                LocalDateTime.of(2025, 10, 1, 12, 0),
                                LocalDateTime.of(2025, 10, 30, 12, 0)
                        )
                ))
                .build();
    }

    public static EventRequest 셜록_앤더슨_dto() {
        return EventRequest.builder()
                .title(셜록_앤더슨.title)
                .venue(셜록_앤더슨.venue)
                .description(셜록_앤더슨.description)
                .posterImageUrl(셜록_앤더슨.posterImageUrl)
                .startDate(셜록_앤더슨.startDate)
                .endDate(셜록_앤더슨.endDate)
                .eventCategory(셜록_앤더슨.eventCategory)
                .runtime(셜록_앤더슨.runtime)
                .ageLimit(셜록_앤더슨.ageLimit)
                .schedules(List.of(
                        new ScheduleInfo(
                                LocalDate.of(2025, 10, 16),
                                LocalTime.of(19, 30),
                                LocalDateTime.of(2025, 9, 1, 12, 0),
                                LocalDateTime.of(2025, 9, 30, 12, 0)
                        )
                ))
                .build();
    }
}
