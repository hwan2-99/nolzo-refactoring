package com.noljo.nolzo.support.fixture;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.entity.Role;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public enum MemberFixture {
    회원("김회원", "member1@gmail.com", "password", LocalDate.of(1994, 4, 4), Role.USER),
    회투("김회투", "member2@gmail.com", "password", LocalDate.of(1994, 5, 4), Role.USER);

    private String name;
    private String email;
    private String password;
    private LocalDate birth;
    private Role role;

    MemberFixture(String name, String email, String password, LocalDate birth, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birth = birth;
        this.role = role;
    }

    public static Member 회원() {
        return Member.of(회원.name, 회원.email, 회원.password, 회원.birth, 회원.role);
    }
    public static Member 회투() {
        return Member.of(회투.name, 회투.email, 회투.password, 회투.birth, 회투.role);
    }
}
