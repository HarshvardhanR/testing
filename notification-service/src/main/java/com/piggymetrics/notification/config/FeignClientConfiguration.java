package com.piggymetrics.notification.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

public class FeignClientConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor(OAuth2AuthorizedClientManager manager) {
        return requestTemplate -> {
            // This pulls a "client_credentials" token for the notification-service
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId("notification-service")
                    .principal("notification-service")
                    .build();

            OAuth2AuthorizedClient client = manager.authorize(authorizeRequest);

            if (client != null && client.getAccessToken() != null) {
                String token = client.getAccessToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}