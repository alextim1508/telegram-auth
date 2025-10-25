package com.alextim.telegramauth.security;

import com.alextim.telegramauth.entity.TelegramUser;
import com.alextim.telegramauth.repository.TelegramUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramUserDetailsManager implements UserDetailsManager {

    private final TelegramUserRepository telegramUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        return telegramUserRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
    }

    @Override
    public void createUser(UserDetails user) {
        log.debug("Creating user: {}", user.getUsername());
        telegramUserRepository.save((TelegramUser) user);
        log.info("User created: {}", user.getUsername());
    }

    @Override
    public void updateUser(UserDetails user) {
        log.debug("Updating user: {}", user.getUsername());
        telegramUserRepository.save((TelegramUser) user);
        log.info("User updated: {}", user.getUsername());
    }

    @Override
    public void deleteUser(String username) {
        log.debug("Deleting user with username: {}", username);
        telegramUserRepository.deleteById(username);
        log.info("User deleted: {}", username);
    }

    @Override
    public boolean userExists(String username) {
        boolean exists = telegramUserRepository.existsByUsername(username);
        log.debug("Check user exists for username '{}': {}", username, exists);
        return exists;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        log.debug("Change password attempted, but not implemented for Telegram auth.");
    }
}