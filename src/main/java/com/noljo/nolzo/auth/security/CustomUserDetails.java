package com.noljo.nolzo.auth.security;

import com.noljo.nolzo.member.entity.Role;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final Long memberId;
    private final String email;
    private final String password;
    private final Role role;

    public CustomUserDetails(Long memberId, String email, String password, Role role) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getMemberId() {
        return memberId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
