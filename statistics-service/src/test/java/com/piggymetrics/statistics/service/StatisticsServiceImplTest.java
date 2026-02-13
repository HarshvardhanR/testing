package com.piggymetrics.statistics.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.piggymetrics.statistics.domain.*;
import com.piggymetrics.statistics.domain.timeseries.DataPoint;
import com.piggymetrics.statistics.domain.timeseries.ItemMetric;
import com.piggymetrics.statistics.domain.timeseries.StatisticMetric;
import com.piggymetrics.statistics.repository.DataPointRepository;
import org.junit.jupiter.api.Test; // Updated
import org.junit.jupiter.api.extension.ExtendWith; // Added
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // Added

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*; // Updated
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Replaces initMocks
class StatisticsServiceImplTest {

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Mock
    private ExchangeRatesServiceImpl ratesService;

    @Mock
    private DataPointRepository repository;

    @Test
    void shouldFindDataPointListByAccountName() {
        final List<DataPoint> list = ImmutableList.of(new DataPoint());
        when(repository.findByIdAccount("test")).thenReturn(list);

        List<DataPoint> result = statisticsService.findByAccountName("test");
        assertEquals(list, result);
    }

    @Test
    void shouldFailToFindDataPointWhenAccountNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            statisticsService.findByAccountName(null);
        });
    }

    @Test
    void shouldFailToFindDataPointWhenAccountNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            statisticsService.findByAccountName("");
        });
    }

    @Test
    void shouldSaveDataPoint() {

        // --- Given ---
        Item salary = new Item();
        salary.setTitle("Salary");
        salary.setAmount(new BigDecimal(9100));
        salary.setCurrency(Currency.USD);
        salary.setPeriod(TimePeriod.MONTH);

        Item grocery = new Item();
        grocery.setTitle("Grocery");
        grocery.setAmount(new BigDecimal(500));
        grocery.setCurrency(Currency.RUB);
        grocery.setPeriod(TimePeriod.DAY);

        Item vacation = new Item();
        vacation.setTitle("Vacation");
        vacation.setAmount(new BigDecimal(3400));
        vacation.setCurrency(Currency.EUR);
        vacation.setPeriod(TimePeriod.YEAR);

        Saving saving = new Saving();
        saving.setAmount(new BigDecimal(1000));
        saving.setCurrency(Currency.EUR);
        saving.setInterest(new BigDecimal("3.2"));
        saving.setDeposit(true);
        saving.setCapitalization(false);

        Account account = new Account();
        account.setIncomes(ImmutableList.of(salary));
        account.setExpenses(ImmutableList.of(grocery, vacation));
        account.setSaving(saving);

        final Map<Currency, BigDecimal> rates = ImmutableMap.of(
                Currency.EUR, new BigDecimal("0.8"),
                Currency.RUB, new BigDecimal("80"),
                Currency.USD, BigDecimal.ONE
        );

        // --- When ---
        when(ratesService.convert(any(Currency.class), any(Currency.class), any(BigDecimal.class)))
                .thenAnswer(i -> {
                    BigDecimal amount = i.getArgument(2);
                    Currency from = i.getArgument(0);
                    return amount.divide(rates.get(from), 4, RoundingMode.HALF_UP);
                });

        when(ratesService.getCurrentRates()).thenReturn(rates);
        when(repository.save(any(DataPoint.class))).then(returnsFirstArg());

        DataPoint dataPoint = statisticsService.save("test", account);

        // --- Then ---
        final BigDecimal expectedExpensesAmount = new BigDecimal("17.8861");
        final BigDecimal expectedIncomesAmount = new BigDecimal("298.9802");
        final BigDecimal expectedSavingAmount = new BigDecimal("1250");

        final BigDecimal expectedNormalizedSalaryAmount = new BigDecimal("298.9802");
        final BigDecimal expectedNormalizedVacationAmount = new BigDecimal("11.6361");
        final BigDecimal expectedNormalizedGroceryAmount = new BigDecimal("6.25");

        assertEquals("test", dataPoint.getId().getAccount());
        
        // Date comparison using LocalDate logic
        Date expectedDate = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        assertEquals(expectedDate, dataPoint.getId().getDate());

        // Using compareTo for BigDecimal to avoid scale mismatch issues
        assertEquals(0, expectedExpensesAmount.compareTo(dataPoint.getStatistics().get(StatisticMetric.EXPENSES_AMOUNT)));
        assertEquals(0, expectedIncomesAmount.compareTo(dataPoint.getStatistics().get(StatisticMetric.INCOMES_AMOUNT)));
        assertEquals(0, expectedSavingAmount.compareTo(dataPoint.getStatistics().get(StatisticMetric.SAVING_AMOUNT)));

        ItemMetric salaryItemMetric = dataPoint.getIncomes().stream()
                .filter(i -> i.getTitle().equals(salary.getTitle()))
                .findFirst().orElseThrow();

        ItemMetric vacationItemMetric = dataPoint.getExpenses().stream()
                .filter(i -> i.getTitle().equals(vacation.getTitle()))
                .findFirst().orElseThrow();

        ItemMetric groceryItemMetric = dataPoint.getExpenses().stream()
                .filter(i -> i.getTitle().equals(grocery.getTitle()))
                .findFirst().orElseThrow();

        assertEquals(0, expectedNormalizedSalaryAmount.compareTo(salaryItemMetric.getAmount()));
        assertEquals(0, expectedNormalizedVacationAmount.compareTo(vacationItemMetric.getAmount()));
        assertEquals(0, expectedNormalizedGroceryAmount.compareTo(groceryItemMetric.getAmount()));

        assertEquals(rates, dataPoint.getRates());
        verify(repository, times(1)).save(any(DataPoint.class));
    }
}