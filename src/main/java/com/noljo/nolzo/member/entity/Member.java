package com.noljo.nolzo.member.entity;

import com.noljo.nolzo.global.BaseEntity;
import com.noljo.nolzo.payment.entity.Payment;
import com.noljo.nolzo.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Payment> payments = new ArrayList<>();

    // 정적 팩토리 메서드
    public static Member of(String name, String email, String password, LocalDate birth, Role role) {
        Member member = new Member();
        member.name = name;
        member.email = email;
        member.password = password;
        member.birth = birth;
        member.role = role;
        return member;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}