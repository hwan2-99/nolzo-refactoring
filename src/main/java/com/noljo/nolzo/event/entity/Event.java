package com.noljo.nolzo.event.entity;

import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.global.BaseEntity;
import com.noljo.nolzo.seat.entity.Seat;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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

    private LocalDateTime reservationStart;

    private LocalDateTime reservationEnd;

    @Column(nullable = false)
    private long viewCount = 0;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();

    @Builder
    public Event(Long id, String title, String venue, String description, String posterImageUrl, LocalDate startDate, LocalDate endDate,
                 EventCategory eventCategory, int runtime, int ageLimit, int rating, int reviewCount,
                 LocalDateTime reservationStart,LocalDateTime reservationEnd) {
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
        this.reservationStart=reservationStart;
        this.reservationEnd=reservationEnd;
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
        schedule.setEvent(this);
    }

    public void addViewCount() {
        this.viewCount++;
    }

    public void updateFrom(EventUpdateRequest dto) {
        this.title         = dto.getTitle();
        this.venue         = dto.getVenue();
        this.description   = dto.getDescription();
        this.startDate     = dto.getStartDate();
        this.endDate       = dto.getEndDate();
        this.reservationStart=dto.getReservationStart();
        this.reservationEnd=dto.getReservationEnd();
    }
}
