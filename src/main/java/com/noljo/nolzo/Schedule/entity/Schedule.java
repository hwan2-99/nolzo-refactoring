package com.noljo.nolzo.Schedule.entity;

import com.noljo.nolzo.event.entity.Event;
import com.noljo.nolzo.event.entity.EventCategory;
import com.noljo.nolzo.ticket.entity.Ticket;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

//    @OneToMany(mappedBy = "schedule")
//    private List<Ticket> tickets;

    public void setEvent(Event event) {
        this.event = event;
    }

    @Builder
    public Schedule(LocalDate showDate, LocalTime showTime) {
        this.showDate=showDate;
        this.showTime=showTime;
    }

    public void updateFrom(LocalDate showDate, LocalTime showTime){
        this.showDate=showDate;
        this.showTime=showTime;
    }
}
