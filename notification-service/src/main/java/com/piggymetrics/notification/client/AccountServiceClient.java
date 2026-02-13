package com.piggymetrics.notification.client;

import com.piggymetrics.notification.config.FeignClientConfiguration; // Import your new config
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 1. Added the configuration link so this client uses the OAuth2 interceptor
@FeignClient(name = "account-service", configuration = FeignClientConfiguration.class)
public interface AccountServiceClient {

    // 2. Simplified to @GetMapping
    // 3. Changed UTF8_VALUE to just APPLICATION_JSON_VALUE (SB3 standard)
    @GetMapping(value = "/accounts/{accountName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    String getAccount(@PathVariable("accountName") String accountName);

}