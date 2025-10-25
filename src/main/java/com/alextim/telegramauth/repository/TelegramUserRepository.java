package com.alextim.telegramauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alextim.telegramauth.entity.TelegramUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, String> {
    Optional<TelegramUser> findByUsername(String username);

    boolean existsByUsername(String username);
}