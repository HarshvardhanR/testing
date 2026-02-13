package com.piggymetrics.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.beans.factory.ObjectProvider;

@Configuration
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/health", "/info", "/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            // Configures the service to validate incoming JWT tokens
            .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
            // Configures the service to act as an OAuth2 client for Feign/Rest
            .oauth2Client(Customizer.withDefaults());

        return http.build();
    }

    /**
     * This bean handles the logic of getting a fresh token from the Auth service
     * using the client_credentials grant type. 
     * Using ObjectProvider prevents the "Bean Not Found" crash if the config server 
     * properties aren't fully loaded yet.
     */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository,
            ObjectProvider<OAuth2AuthorizedClientRepository> authorizedClientRepository) {

        var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        // getIfAvailable() returns null instead of throwing an Exception
        var manager = new DefaultOAuth2AuthorizedClientManager(
                clientRegistrationRepository.getIfAvailable(), 
                authorizedClientRepository.getIfAvailable());
        
        manager.setAuthorizedClientProvider(authorizedClientProvider);

        return manager;
    }
}