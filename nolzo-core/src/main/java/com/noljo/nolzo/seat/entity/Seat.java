package com.noljo.nolzo.seat.entity;

import com.noljo.nolzo.global.BaseEntity;
import com.noljo.nolzo.schedule.entity.Schedule;
import com.noljo.nolzo.ticket.entity.Ticket;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    private String rowName;

    private int seatNumber;

    private String seatSection;

    private String floor;

    private int price;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.PERSIST)
    private List<Ticket> tickets = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Builder
    public Seat(Long id, String rowName, int seatNumber, String seatSection, String floor, int price, SeatStatus status,
                Schedule schedule) {
        this.id = id;
        this.rowName = rowName;
        this.seatNumber = seatNumber;
        this.seatSection = seatSection;
        this.floor = floor;
        this.price = price;
        this.status = status;
        this.schedule = schedule;
    }

    private Seat(Long id, String rowName, int seatNumber, String seatSection, String floor, int price,
                 SeatStatus status,
                 List<Ticket> tickets, Schedule schedule) {
        this.id = id;
        this.rowName = rowName;
        this.seatNumber = seatNumber;
        this.seatSection = seatSection;
        this.floor = floor;
        this.price = price;
        this.status = status;
        this.tickets = tickets;
        this.schedule = schedule;
    }

    public Seat(String rowName, int seatNumber, String seatSection, String floor, int price, SeatStatus seatStatus,
                Schedule schedule) {
        this.rowName = rowName;
        this.seatNumber = seatNumber;
        this.seatSection = seatSection;
        this.floor = floor;
        this.price = price;
        this.status = seatStatus;
        this.schedule = schedule;
    }

    public void updateStatus(SeatStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", rowName='" + rowName + '\'' +
                ", seatNumber=" + seatNumber +
                ", seatSection='" + seatSection + '\'' +
                ", floor='" + floor + '\'' +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}
