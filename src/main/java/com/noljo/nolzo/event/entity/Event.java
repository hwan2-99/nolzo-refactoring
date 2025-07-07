package com.noljo.nolzo.event.entity;

import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.global.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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


    @Column(nullable = false)
    private long viewCount = 0;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Schedule> schedules = new ArrayList<>();

    @Builder
    public Event(Long id, String title, String venue, String description, String posterImageUrl, LocalDate startDate, LocalDate endDate,
                 EventCategory eventCategory, int runtime, int ageLimit) {
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
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
        schedule.setEvent(this);
    }

    public void addViewCount() {
        this.viewCount++;
    }

    public void updateFrom(EventUpdateRequest dto) {
        Optional.ofNullable(dto.getTitle()).ifPresent(t -> this.title = t);
        Optional.ofNullable(dto.getVenue()).ifPresent(v -> this.venue = v);
        Optional.ofNullable(dto.getDescription()).ifPresent(d -> this.description = d);
        Optional.ofNullable(dto.getStartDate()).ifPresent(sd -> this.startDate = sd);
        Optional.ofNullable(dto.getEndDate()).ifPresent(ed -> this.endDate = ed);
    }

}
