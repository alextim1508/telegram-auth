package com.alextim.telegramauth.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.telegram")
@Data
public class TelegramAuthProperties {
    private String botToken;
    private String botName;
}