package com.alextim.telegramauth.security;

import com.alextim.telegramauth.entity.TelegramUser;
import com.alextim.telegramauth.service.TelegramAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramUserDetailsAuthProvider implements AuthenticationProvider {

    private final TelegramAuthService telegramAuthService;
    private final UserDetailsManager userDetailsManager;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("Attempting Telegram authentication for token: {}", authentication);

        Map<String, Object> credentials = (Map<String, Object>) authentication.getCredentials();

        try {
            if (telegramAuthService.validateTelegramData(credentials)) {
                int telegramId = (int)credentials.get("id");
                String telegramUsername = (String) credentials.get("username");

                TelegramUser telegramUser = new TelegramUser(
                        telegramId,
                        telegramUsername,
                        (String) credentials.get("first_name"),
                        (String) credentials.get("last_name"),
                        (String) credentials.get("photo_url")
                );

                log.info("Successfully validated data for Telegram user ID: {}", telegramUsername);
                save(telegramUser);

                UserDetails userDetails = userDetailsManager.loadUserByUsername(telegramUser.getUsername());
                log.debug("Loaded UserDetails for username: {}", telegramUser.getUsername());

                return TelegramAuthToken.authenticated(userDetails);
            } else {
                log.warn("Telegram data validation failed for data: {}", credentials);
                throw new InternalAuthenticationServiceException("Telegram data is not valid");
            }

        } catch (UsernameNotFoundException exception) {
            log.error("User not found during authentication: {}", exception.getMessage());
            throw exception;

        } catch (Exception exception) {
            log.error("Problem accessing user details: {}", exception.getMessage(), exception);
            throw new InternalAuthenticationServiceException(
                    "Problem accessing user details: " + exception.getMessage(),
                    exception
            );
        }
    }

    private void save(UserDetails user) {
        if (userDetailsManager.userExists(user.getUsername())) {
            log.debug("Updating existing user: {}", user.getUsername());
            userDetailsManager.updateUser(user);
        } else {
            log.debug("Creating new user: {}", user.getUsername());
            userDetailsManager.createUser(user);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TelegramAuthToken.class.isAssignableFrom(authentication);
    }
}