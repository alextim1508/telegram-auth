package com.alextim.telegramauth.config;

import com.alextim.telegramauth.repository.TelegramUserRepository;
import com.alextim.telegramauth.security.TelegramAuthFilter;
import com.alextim.telegramauth.security.TelegramUserDetailsAuthProvider;
import com.alextim.telegramauth.security.TelegramUserDetailsManager;
import com.alextim.telegramauth.service.TelegramAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String LOGIN_URL = "/login";

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            HttpSessionSecurityContextRepository contextRepository,
            AuthenticationManager authenticationManager) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/hello/public", LOGIN_URL).permitAll()
                        .requestMatchers("/hello/private").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage(LOGIN_URL)
                        .loginProcessingUrl(LOGIN_URL)
                        .permitAll()
                )
                .addFilterAt(
                        createTelegramAuthFilter(contextRepository, authenticationManager),
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    private TelegramAuthFilter createTelegramAuthFilter(HttpSessionSecurityContextRepository contextRepository,
                                                        AuthenticationManager authenticationManager) {
        TelegramAuthFilter filter = new TelegramAuthFilter(
                new AntPathRequestMatcher(LOGIN_URL, HttpMethod.POST.name()),
                authenticationManager
        );
        filter.setSecurityContextRepository(contextRepository);
        return filter;
    }

    @Bean
    public UserDetailsManager userDetailsManager(TelegramUserRepository telegramUserRepository) {
        return new TelegramUserDetailsManager(telegramUserRepository);
    }

    @Bean
    public AuthenticationProvider telegramAuthProvider(TelegramAuthService telegramAuthService,
                                                       UserDetailsManager userDetailsManager) {
        return new TelegramUserDetailsAuthProvider(telegramAuthService, userDetailsManager);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider telegramAuthProvider) {
        ProviderManager providerManager = new ProviderManager(telegramAuthProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    @Bean
    public HttpSessionSecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
}