package com.alextim.telegramauth.security;

import com.alextim.telegramauth.entity.TelegramUser;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

@Getter
public class TelegramAuthToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final Object credentials;

    public static TelegramAuthToken unauthenticated(Map<String, Object> data) {
        return new TelegramAuthToken(
                data.get("id"),
                data,
                false
        );
    }

    public static TelegramAuthToken authenticated(UserDetails userDetails) {
        return new TelegramAuthToken(
                userDetails,
                userDetails,
                true
        );
    }

    private TelegramAuthToken(Object principal, Object credentials, boolean authenticated) {
        super(TelegramUser.DEFAULT_AUTHORITIES);
        this.principal = principal;
        this.credentials = credentials;

        setAuthenticated(authenticated);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}