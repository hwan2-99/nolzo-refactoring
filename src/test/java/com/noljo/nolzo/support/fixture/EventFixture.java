package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.event.dto.EventRequest;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public enum EventFixture {
    캣츠("Cats", "서울 예술의 전당", "세계적으로 유명한 뮤지컬, 고양이들의 이야기를 그린 작품입니다.",
            LocalDate.of(2024, 3, 1), LocalDate.of(2024, 6, 30), EventCategory.CONCERT, 12, 5, 120)
,
    햄릿("Hamlet", "국립극장 해오름극장", "셰익스피어 4대 비극 중 하나인 ‘햄릿’의 현대적 재해석 공연입니다.", LocalDate.of(2025, 7, 1),
        LocalDate.of(2025, 7, 15), EventCategory.CONCERT, 15, 4, 80);

    private String title;
    private String venue;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private EventCategory eventCategory;
    private int ageLimit;
    private int rating;
    private int reviewCount;

    EventFixture(String title, String venue, String description, LocalDate startDate, LocalDate endDate,
                 EventCategory eventCategory, int ageLimit, int rating, int reviewCount) {
        this.title = title;
        this.venue = venue;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventCategory = eventCategory;
        this.ageLimit = ageLimit;
        this.rating = rating;
        this.reviewCount = reviewCount;
    }

    public static Event 캣츠() {
        return new Event(null, 캣츠.title, 캣츠.venue, 캣츠.description, 캣츠.startDate, 캣츠.endDate, 캣츠.eventCategory,
                캣츠.ageLimit, 캣츠.rating, 캣츠.reviewCount);
    }
    public static EventRequest 캣츠dto() {
    return EventRequest.builder()
            .title(캣츠.title)
            .venue(캣츠.venue)
            .description(캣츠.description)
            .startDate(캣츠.startDate)
            .endDate(캣츠.endDate)
            .eventCategory(캣츠.eventCategory)
            .ageLimit(캣츠.ageLimit)
            .build();
    }
    public static EventRequest 햄릿dto() {
        return EventRequest.builder()
                .title(햄릿.title)
                .venue(햄릿.venue)
                .description(햄릿.description)
                .startDate(햄릿.startDate)
                .endDate(햄릿.endDate)
                .eventCategory(햄릿.eventCategory)
                .ageLimit(햄릿.ageLimit)
                .build();
    }
}
