package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.member.entity.Member;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public enum MemberFixture {
    회원("김회원", "member@gmail.com", "password", LocalDate.of(1994, 4, 4)),
    회투("김회투", "member@gmail.com", "password", LocalDate.of(1994, 5, 4));
    private String name;
    private String email;
    private String password;
    private LocalDate birth;

    MemberFixture(String name, String email, String password, LocalDate birth) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birth = birth;
    }

    public static Member 회원() {
        return new Member(null, 회원.name, 회원.email, 회원.password, 회원.birth);
    }
    public static Member 회투() {
        return new Member(null, 회투.name, 회투.email, 회투.password, 회투.birth);
    }
}
