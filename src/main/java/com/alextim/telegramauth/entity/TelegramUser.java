package com.alextim.telegramauth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "telegram_users")
public class TelegramUser implements UserDetails {

    public static final List<SimpleGrantedAuthority> DEFAULT_AUTHORITIES =
            List.of(new SimpleGrantedAuthority("USER"));

    public static final String DEFAULT_PASSWORD = "No password";

    @Id
    private Integer telegramId;

    private String username;

    private String firstName;

    private String lastName;

    private String photoUrl;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return DEFAULT_AUTHORITIES;
    }

    @Override
    public String getPassword() {
        return DEFAULT_PASSWORD;
    }
}