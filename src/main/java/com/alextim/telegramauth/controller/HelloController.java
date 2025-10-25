package com.alextim.telegramauth.controller;

import com.alextim.telegramauth.entity.TelegramUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
@RequiredArgsConstructor
public class HelloController {

    @GetMapping("/public")
    public String helloWorld() {
        return "Hello";
    }

    @GetMapping("/private")
    public String helloWorldPerson(@AuthenticationPrincipal TelegramUser user) {
        return "Hello, " + user.getUsername();
    }
}
