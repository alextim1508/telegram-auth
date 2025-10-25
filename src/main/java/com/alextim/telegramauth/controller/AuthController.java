package com.alextim.telegramauth.controller;

import com.alextim.telegramauth.property.AppProperties;
import com.alextim.telegramauth.property.TelegramAuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthController {

    private final TelegramAuthProperties telegramAuthProperties;
    private final AppProperties appProperties;

    @GetMapping
    public String getAuthScript(Model model) {
        model.addAttribute("botName", telegramAuthProperties.getBotName());
        model.addAttribute("baseUrl", appProperties.getBaseUrl());
        return "telegramAuth";
    }
}