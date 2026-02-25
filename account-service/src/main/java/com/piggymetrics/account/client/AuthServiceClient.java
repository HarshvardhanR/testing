package com.piggymetrics.account.client;

import com.piggymetrics.account.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// We add the url property to point to the environment variable we'll set in Docker
@FeignClient(name = "auth-service", url = "${auth.service.url:http://auth-service:8080}")
public interface AuthServiceClient {

    // 1. Removed /uaa from the path
    // 2. Switched to APPLICATION_JSON_VALUE (UTF8 is deprecated in newer Spring versions)
    @RequestMapping(method = RequestMethod.POST, value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    void createUser(User user);

}