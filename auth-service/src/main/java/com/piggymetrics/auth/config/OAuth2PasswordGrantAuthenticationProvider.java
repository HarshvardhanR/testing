package com.piggymetrics.auth.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.util.Collections;

public class OAuth2PasswordGrantAuthenticationProvider implements AuthenticationProvider {
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final AuthenticationProvider daoAuthenticationProvider; // This is your Mongo-backed provider

    public OAuth2PasswordGrantAuthenticationProvider(OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator, 
                                                    AuthenticationProvider daoAuthenticationProvider) {
        this.tokenGenerator = tokenGenerator;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2PasswordGrantAuthenticationToken passwordGrantAuthentication = (OAuth2PasswordGrantAuthenticationToken) authentication;
        OAuth2ClientAuthenticationToken clientPrincipal = (OAuth2ClientAuthenticationToken) passwordGrantAuthentication.getPrincipal();
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        // 1. Validate the User Credentials against MongoDB
        String username = (String) passwordGrantAuthentication.getAdditionalParameters().get("username");
        String password = (String) passwordGrantAuthentication.getAdditionalParameters().get("password");
        Authentication userAuth = daoAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // 2. Generate the Access Token
        OAuth2TokenContext tokenContext = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(userAuth)
                .authorizationServerContext(null)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .build();

        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(), 
                generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());

        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2PasswordGrantAuthenticationToken.class.isAssignableFrom(authentication);
    }
}