package com.noljo.nolzo.auth.security;

import com.noljo.nolzo.member.entity.Member;
import com.noljo.nolzo.member.entity.Role;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final String email;
    private final Role role;

    private CustomUserDetails(String email, Role role) {
        this.email = email;
        this.role = role;
    }

    public static CustomUserDetails fromJwtClaims(String email, Role role) {
        return new CustomUserDetails(email, role);
    }

    @Override
    public String getPassword() {
        return null;
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
