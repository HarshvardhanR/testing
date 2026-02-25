package com.piggymetrics.account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
@EnableWebSecurity
public class ResourceServerConfig {

    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(csrf -> csrf.disable())
    //         // 1. Ensure the app doesn't try to create a session
    //         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //         .authorizeHttpRequests(auth -> auth
    //             // 2. Permit the root and empty paths for registration
    //             .requestMatchers(HttpMethod.POST, "/", "").permitAll()
    //             // 3. Permit internal error handling (Essential in Spring Boot 3)
    //             .requestMatchers("/error", "/error/**").permitAll()
    //             // 4. Require auth for everything else
    //             .anyRequest().authenticated()
    //         )
    //         // 5. Configure the Resource Server
    //         .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));

    //     return http.build();
    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for the POST request
            .authorizeHttpRequests(auth -> auth
                // The 'anyRequest().permitAll()' is for testing only 
                // to ensure the 401 isn't a networking/Docker issue
                .anyRequest().permitAll() 
            );
        return http.build();
    }

    /**
     * Modern Feign Interceptor replacement.
     * This ensures the JWT token received by the account-service is passed 
     * along to any downstream services (like statistics-service) called via Feign.
     */
    @Bean
    public feign.RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getCredentials() instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
                requestTemplate.header("Authorization", "Bearer " + jwt.getTokenValue());
            }
        };
    }
}