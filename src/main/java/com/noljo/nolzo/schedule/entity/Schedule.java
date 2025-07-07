package com.noljo.nolzo.schedule.entity;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.seat.entity.Seat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @Column
    private LocalDate showDate;

    @Column
    private LocalTime showTime;

    private LocalDateTime reservationStart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();

    public void setEvent(Event event) {
        this.event = event;
    }

    @Builder
    public Schedule(LocalDate showDate, LocalTime showTime, Event event,LocalDateTime reservationStart) {
        this.showDate = showDate;
        this.showTime = showTime;
        this.reservationStart=reservationStart;
        this.event = event;
    }

    public void updateFrom(LocalDate showDate, LocalTime showTime) {
        this.showDate = showDate;
        this.showTime = showTime;
    }
}
