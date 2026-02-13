package com.piggymetrics.account.client;

import com.piggymetrics.account.domain.Account;
import org.junit.jupiter.api.Test; // JUnit 5
import org.junit.jupiter.api.extension.ExtendWith; // JUnit 5
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput; // Modern OutputCapture
import org.springframework.boot.test.system.OutputCaptureExtension; // Modern OutputCapture

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@ExtendWith(OutputCaptureExtension.class) // Injects output capture support
@SpringBootTest(properties = {
        "spring.cloud.openfeign.circuitbreaker.enabled=true" // Hystrix is replaced by CircuitBreaker
})
class StatisticsServiceClientFallbackTest {

    @Autowired
    private StatisticsServiceClient statisticsServiceClient;

    @Test
    void testUpdateStatisticsWithFailFallback(CapturedOutput output) { // Inject output here
        statisticsServiceClient.updateStatistics("test", new Account());

        // Modern check: output.getOut() returns the console string
        assertThat(output.getOut(), containsString("Error during update statistics for account: test"));
    }
}