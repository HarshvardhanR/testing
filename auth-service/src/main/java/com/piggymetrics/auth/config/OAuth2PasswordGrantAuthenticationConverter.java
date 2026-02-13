package com.piggymetrics.auth.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OAuth2PasswordGrantAuthenticationConverter implements AuthenticationConverter {
    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!"password".equals(grantType)) return null;

        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        request.getParameterMap().forEach((key, value) -> parameters.add(key, value[0]));

        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        Set<String> requestedScopes = (scope != null) ? 
            new HashSet<>(Arrays.asList(scope.split(" "))) : null;

        Map<String, Object> additionalParameters = parameters.entrySet().stream()
                .filter(e -> !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE) &&
                             !e.getKey().equals(OAuth2ParameterNames.SCOPE))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

        if (requestedScopes != null) {
            additionalParameters.put("scope", requestedScopes);
        }

        return new OAuth2PasswordGrantAuthenticationToken(clientPrincipal, requestedScopes, additionalParameters);
    }
}