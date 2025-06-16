package com.noljo.nolzo.event.entity;

import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.seat.entity.Seat;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    private String title;

    private String venue;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private EventCategory eventCategory;

    private int ageLimit;

    private int rating;

    private int reviewCount;

    @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST)
    private List<Seat> seats = new ArrayList<>();

    @Builder
    public Event(Long id, String title, String venue, String description, LocalDate startDate, LocalDate endDate,
                 EventCategory eventCategory, int ageLimit, int rating, int reviewCount) {
        this.id = id;
        this.title = title;
        this.venue = venue;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventCategory = eventCategory;
        this.ageLimit = ageLimit;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.seats = new ArrayList<>();
    }
}
