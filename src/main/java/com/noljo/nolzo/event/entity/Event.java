package com.noljo.nolzo.event.entity;
import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.Schedule.entity.Schedule;
import com.noljo.nolzo.global.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    private String title;

    private String venue;

    private String description;

    private String posterImageUrl;

    private LocalDate startDate;

    private LocalDate endDate;


    @Enumerated(EnumType.STRING)
    private EventCategory eventCategory;

    private int runtime;

    private int ageLimit;

    private int rating;

    private int reviewCount;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Schedule> schedules = new ArrayList<>();


    @Builder
    public Event(Long id, String title, String venue, String description, String posterImageUrl, LocalDate startDate, LocalDate endDate,
                 EventCategory eventCategory, int runtime, int ageLimit, int rating, int reviewCount) {
        this.id = id;
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
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
        schedule.setEvent(this);
    }
    public void updateFrom(EventUpdateRequest dto) {
        this.title         = dto.getTitle();
        this.venue         = dto.getVenue();
        this.description   = dto.getDescription();
        this.startDate     = dto.getStartDate();
        this.endDate       = dto.getEndDate();
    }
}
