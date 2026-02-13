package com.piggymetrics.gateway;

import org.junit.jupiter.api.Test; // Changed import
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // @RunWith is gone! It's handled automatically now.
class GatewayApplicationTests { // 'public' is optional in JUnit 5

    @Test
    void contextLoads() {
    }

    @Test
    void fire() {
    }

}