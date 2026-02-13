package com.piggymetrics.statistics.client;

import com.piggymetrics.statistics.domain.Currency;
import com.piggymetrics.statistics.domain.ExchangeRatesContainer;
import org.junit.jupiter.api.Test; // Updated to JUnit 5
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals; // Updated to JUnit 5
import static org.junit.jupiter.api.Assertions.assertNotNull; // Updated to JUnit 5

@SpringBootTest
class ExchangeRatesClientTest { // JUnit 5 tests can be package-private

    @Autowired
    private ExchangeRatesClient client;

    @Test
    void shouldRetrieveExchangeRates() {

        ExchangeRatesContainer container = client.getRates(Currency.getBase());

        assertEquals(LocalDate.now(), container.getDate()); // Note: JUnit 5 is (expected, actual)
        assertEquals(Currency.getBase(), container.getBase());

        assertNotNull(container.getRates());
        assertNotNull(container.getRates().get(Currency.USD.name()));
        assertNotNull(container.getRates().get(Currency.EUR.name()));
        assertNotNull(container.getRates().get(Currency.RUB.name()));
    }

    @Test
    void shouldRetrieveExchangeRatesForSpecifiedCurrency() {

        Currency requestedCurrency = Currency.EUR;
        ExchangeRatesContainer container = client.getRates(Currency.getBase());

        assertEquals(LocalDate.now(), container.getDate());
        assertEquals(Currency.getBase(), container.getBase());

        assertNotNull(container.getRates());
        assertNotNull(container.getRates().get(requestedCurrency.name()));
    }
}