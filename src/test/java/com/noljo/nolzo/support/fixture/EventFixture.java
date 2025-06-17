package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.event.dto.EventRequestDto;
import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public enum EventFixture {
    캣츠("Cats", "서울 예술의 전당", "세계적으로 유명한 뮤지컬, 고양이들의 이야기를 그린 작품입니다.",
            LocalDate.of(2024, 3, 1), LocalDate.of(2024, 6, 30), EventCategory.CONCERT, 12, 5, 120);
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
    public static EventRequestDto 캣츠dto() {
    return EventRequestDto.builder()
            .title(캣츠.title)
            .venue(캣츠.venue)
            .description(캣츠.description)
            .startDate(캣츠.startDate)
            .endDate(캣츠.endDate)
            .eventCategory(캣츠.eventCategory)
            .ageLimit(캣츠.ageLimit)
            .build();
}
}
