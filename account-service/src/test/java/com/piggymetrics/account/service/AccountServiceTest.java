package com.piggymetrics.account.service;

import com.piggymetrics.account.client.AuthServiceClient;
import com.piggymetrics.account.client.StatisticsServiceClient;
import com.piggymetrics.account.domain.*;
import com.piggymetrics.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach; // Changed
import org.junit.jupiter.api.Test; // Changed
import org.junit.jupiter.api.extension.ExtendWith; // Added
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // Added

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*; // Changed
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Handles initMocks automatically
class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private StatisticsServiceClient statisticsClient;

    @Mock
    private AuthServiceClient authClient;

    @Mock
    private AccountRepository repository;

    @Test
    void shouldFindByName() {
        final Account account = new Account();
        account.setName("test");

        // Note: when calling a mock, use the mock object, not the service itself
        when(repository.findByName(account.getName())).thenReturn(account);
        Account found = accountService.findByName(account.getName());

        assertEquals(account, found);
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        // JUnit 5 way to test exceptions
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.findByName("");
        });
    }

    @Test
    void shouldCreateAccountWithGivenUser() {
        User user = new User();
        user.setUsername("test");

        Account account = accountService.create(user);

        assertEquals(user.getUsername(), account.getName());
        assertEquals(0, account.getSaving().getAmount().intValue());
        assertEquals(Currency.getDefault(), account.getSaving().getCurrency());
        assertEquals(0, account.getSaving().getInterest().intValue());
        assertFalse(account.getSaving().getDeposit());
        assertFalse(account.getSaving().getCapitalization());
        assertNotNull(account.getLastSeen());

        verify(authClient, times(1)).createUser(user);
        verify(repository, times(1)).save(account);
    }

    @Test
    void shouldSaveChangesWhenUpdatedAccountGiven() {
        Item grocery = new Item();
        grocery.setTitle("Grocery");
        grocery.setAmount(new BigDecimal(10));
        grocery.setCurrency(Currency.USD);
        grocery.setPeriod(TimePeriod.DAY);
        grocery.setIcon("meal");

        Item salary = new Item();
        salary.setTitle("Salary");
        salary.setAmount(new BigDecimal(9100));
        salary.setCurrency(Currency.USD);
        salary.setPeriod(TimePeriod.MONTH);
        salary.setIcon("wallet");

        Saving saving = new Saving();
        saving.setAmount(new BigDecimal(1500));
        saving.setCurrency(Currency.USD);
        saving.setInterest(new BigDecimal("3.32"));
        saving.setDeposit(true);
        saving.setCapitalization(false);

        final Account update = new Account();
        update.setName("test");
        update.setNote("test note");
        update.setIncomes(Arrays.asList(salary));
        update.setExpenses(Arrays.asList(grocery));
        update.setSaving(saving);

        final Account account = new Account();
        // Correcting the mock: stub the repository, not the service being tested
        when(repository.findByName("test")).thenReturn(account);
        
        accountService.saveChanges("test", update);

        assertEquals(update.getNote(), account.getNote());
        assertNotNull(account.getLastSeen());
        assertEquals(update.getSaving().getAmount(), account.getSaving().getAmount());
        assertEquals(update.getExpenses().size(), account.getExpenses().size());
        
        verify(repository, times(1)).save(account);
        verify(statisticsClient, times(1)).updateStatistics("test", account);
    }

    @Test
    void shouldFailWhenNoAccountsExistedWithGivenName() {
        final Account update = new Account();
        update.setIncomes(Arrays.asList(new Item()));
        update.setExpenses(Arrays.asList(new Item()));

        when(repository.findByName("test")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            accountService.saveChanges("test", update);
        });
    }
}