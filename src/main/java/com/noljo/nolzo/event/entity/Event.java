package com.noljo.nolzo.event.entity;

import com.noljo.nolzo.event.dto.EventUpdateRequest;
import com.noljo.nolzo.schedule.dto.internal.ScheduleInfo;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.global.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
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
        validAddSchedule(schedule);
        schedules.add(schedule);
        schedule.setEvent(this);
    }


    public void addViewCount() {
        this.viewCount++;
    }

    public void validUpdateSchedule(Schedule dto) {
        if (dto.getReservationStart().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("예약시점 후에는 스케줄 변경 불가");
        }
    }

    public void validAddSchedule(Schedule dto) {
        LocalDateTime startEvent = LocalDateTime.of(this.startDate.minusDays(1), LocalTime.of(23, 59));
        LocalDateTime endEvent = LocalDateTime.of(this.endDate, LocalTime.of(23, 59));
        if (startEvent.isAfter(LocalDateTime.of(dto.getShowDate(), dto.getShowTime()))) {
            throw new IllegalStateException("시작시점 전에는 스케줄 추가 불가");
        }
        if (endEvent.isBefore(LocalDateTime.of(dto.getShowDate(), dto.getShowTime()))) {
            throw new IllegalStateException("종료시점 후에는 스케줄 추가 불가");
        }
    }


    public void updateFrom(EventUpdateRequest dto) {

        Optional.ofNullable(dto.getTitle()).ifPresent(t -> this.title = t);
        Optional.ofNullable(dto.getVenue()).ifPresent(v -> this.venue = v);
        Optional.ofNullable(dto.getDescription()).ifPresent(d -> this.description = d);
        Optional.ofNullable(dto.getStartDate()).ifPresent(sd -> this.startDate = sd);
        Optional.ofNullable(dto.getEndDate()).ifPresent(ed -> this.endDate = ed);
        if (dto.getSchedules() != null) {
            Map<Long, Schedule> originalSchedule = schedules.stream()
                    .collect(Collectors.toMap(Schedule::getId, Function.identity()));
            for (ScheduleInfo schedule : dto.getSchedules()) {
                manageSchedule(schedule,originalSchedule);
            }
            for (Schedule removeTarget : originalSchedule.values()) {
                schedules.remove(removeTarget);
            }
        }
    }

    public void manageSchedule(ScheduleInfo schedule, Map<Long, Schedule> originalSchedule) {
        if (schedule.getId() != null && originalSchedule.containsKey(schedule.getId())) {
            Schedule selectedSchedule = originalSchedule.remove(schedule.getId());
            validUpdateSchedule(selectedSchedule);
            selectedSchedule.updateFrom(schedule.getShowDate(), schedule.getShowTime(), schedule.getReservationStart(), schedule.getReservationEnd());
        } else {
            Schedule newSchedule = Schedule.builder().showDate(schedule.getShowDate())
                    .showTime(schedule.getShowTime())
                    .reservationStart(schedule.getReservationStart())
                    .reservationEnd(schedule.getReservationEnd())
                    .build();
            newSchedule.setEvent(this);
            schedules.add(newSchedule);
        }
    }

}
