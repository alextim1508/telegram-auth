package com.alextim.telegramauth.security;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class TelegramAuthFilter extends AbstractAuthenticationProcessingFilter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final AuthenticationManager authenticationManager;

    public TelegramAuthFilter(AntPathRequestMatcher antPathRequestMatcher,AuthenticationManager authenticationManager) {
        super(antPathRequestMatcher);
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,HttpServletResponse response) throws AuthenticationException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        TelegramAuthToken token = createTelegramAuthToken(request);
        if (token == null) {
            throw new AuthenticationServiceException("Could not parse authentication request body");
        }

        return authenticationManager.authenticate(token);
    }

    private TelegramAuthToken createTelegramAuthToken(HttpServletRequest request) {
        Map<String, Object> body = getBody(request);
        if (body == null || body.isEmpty()) {
            return null;
        }
        return TelegramAuthToken.unauthenticated(body);
    }

    private static Map<String, Object> getBody(HttpServletRequest request) {
        try {
            Map<String, Object> body = OBJECT_MAPPER.readValue(request.getInputStream(), new TypeReference<Map<String, Object>>() {});
            log.debug("Raw request body parsed as Map: {}", body);
            return body;
        } catch (IOException e) {
            log.warn("Can't parse request body with error: {}", e.getMessage(), e);
            return null;
        }
    }
}