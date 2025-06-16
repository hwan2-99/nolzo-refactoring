package com.noljo.nolzo.member.entity;

import com.noljo.nolzo.global.BaseEntity;
import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.reservation.entity.Reservation;
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
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    private String email;

    private String password;

    private LocalDate birth;

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Payment> payments = new ArrayList<>();

    @Builder
    public Member(Long id, String name, String email, String password, LocalDate birth) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.birth = birth;
    }
}
