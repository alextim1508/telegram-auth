package com.alextim.telegramauth.controller;

import com.alextim.telegramauth.property.AppProperties;
import com.alextim.telegramauth.property.TelegramAuthProperties;
import com.alextim.telegramauth.service.TelegramAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/auth/telegram")
@RequiredArgsConstructor
public class AuthController {

    private final TelegramAuthService telegramAuthService;
    private final TelegramAuthProperties telegramAuthProperties;
    private final AppProperties appProperties;

    @GetMapping
    public String getAuthScript(Model model) {
        model.addAttribute("botName", telegramAuthProperties.getBotName());
        model.addAttribute("baseUrl", appProperties.getBaseUrl());
        return "telegramAuth";
    }

    @PostMapping("/token")
    @ResponseBody
    public ResponseEntity<String> authenticate(@RequestBody Map<String, Object> telegramData) {
        log.info("Received authentication request from Telegram: {}", telegramData);

        if (telegramAuthService.validateTelegramData(telegramData)) {
            log.info("Authentication successful for data: {}", telegramData);
            return ResponseEntity.ok("success");
        } else {
            log.warn("Authentication failed for data: {}", telegramData);
            return ResponseEntity.badRequest().body("error");
        }
    }
}