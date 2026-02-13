package com.piggymetrics.auth.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;
import java.util.Set;

public class OAuth2PasswordGrantAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {
    public OAuth2PasswordGrantAuthenticationToken(Authentication clientPrincipal, 
                                                Set<String> scopes, Map<String, Object> additionalParameters) {
        super(new AuthorizationGrantType("password"), clientPrincipal, additionalParameters);
    }
}