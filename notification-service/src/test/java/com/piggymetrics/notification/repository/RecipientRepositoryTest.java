package com.piggymetrics.notification.repository;

import com.google.common.collect.ImmutableMap;
import com.piggymetrics.notification.domain.Frequency;
import com.piggymetrics.notification.domain.NotificationSettings;
import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import org.apache.commons.lang3.time.DateUtils; // Updated to lang3 if necessary
import org.junit.jupiter.api.Test; // Updated
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals; // Updated
import static org.junit.jupiter.api.Assertions.assertFalse; // Updated
import static org.junit.jupiter.api.Assertions.assertTrue; // Updated

@DataMongoTest
class RecipientRepositoryTest {

    @Autowired
    private RecipientRepository repository;

    @Test
    void shouldFindByAccountName() {

        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(new Date(0));

        NotificationSettings backup = new NotificationSettings();
        backup.setActive(false);
        backup.setFrequency(Frequency.MONTHLY);
        backup.setLastNotified(new Date());

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.BACKUP, backup,
                NotificationType.REMIND, remind
        ));

        repository.save(recipient);

        Recipient found = repository.findByAccountName(recipient.getAccountName());
        assertEquals(recipient.getAccountName(), found.getAccountName());
        assertEquals(recipient.getEmail(), found.getEmail());

        assertEquals(recipient.getScheduledNotifications().get(NotificationType.BACKUP).getActive(),
                found.getScheduledNotifications().get(NotificationType.BACKUP).getActive());
        assertEquals(recipient.getScheduledNotifications().get(NotificationType.BACKUP).getFrequency(),
                found.getScheduledNotifications().get(NotificationType.BACKUP).getFrequency());
        
        // Note: Date comparison can sometimes be tricky with MongoDB precision; 
        // if this fails, compare .getTime() or use LocalDate
        assertEquals(recipient.getScheduledNotifications().get(NotificationType.BACKUP).getLastNotified(),
                found.getScheduledNotifications().get(NotificationType.BACKUP).getLastNotified());
    }

    @Test
    void shouldFindReadyForRemindWhenFrequencyIsWeeklyAndLastNotifiedWas8DaysAgo() {

        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(DateUtils.addDays(new Date(), -8));

        Recipient recipient = new Recipient();
        recipient.setAccountName("test-remind");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.REMIND, remind
        ));

        repository.save(recipient);

        List<Recipient> found = repository.findReadyForRemind();
        assertFalse(found.isEmpty());
    }

    @Test
    void shouldNotFindReadyForRemindWhenFrequencyIsWeeklyAndLastNotifiedWasYesterday() {

        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(DateUtils.addDays(new Date(), -1));

        Recipient recipient = new Recipient();
        recipient.setAccountName("test-yesterday");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.REMIND, remind
        ));

        repository.save(recipient);

        List<Recipient> found = repository.findReadyForRemind();
        // Since other tests might share the DB, we filter for our specific account or clear the DB
        boolean foundCurrent = found.stream().anyMatch(r -> r.getAccountName().equals("test-yesterday"));
        assertFalse(foundCurrent);
    }

    @Test
    void shouldNotFindReadyForRemindWhenNotificationIsNotActive() {

        NotificationSettings remind = new NotificationSettings();
        remind.setActive(false);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(DateUtils.addDays(new Date(), -30));

        Recipient recipient = new Recipient();
        recipient.setAccountName("test-inactive");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.REMIND, remind
        ));

        repository.save(recipient);

        List<Recipient> found = repository.findReadyForRemind();
        boolean foundCurrent = found.stream().anyMatch(r -> r.getAccountName().equals("test-inactive"));
        assertFalse(foundCurrent);
    }

    @Test
    void shouldNotFindReadyForBackupWhenFrequencyIsQuaterly() {

        NotificationSettings backup = new NotificationSettings();
        backup.setActive(true);
        backup.setFrequency(Frequency.QUARTERLY);
        backup.setLastNotified(DateUtils.addDays(new Date(), -91));

        Recipient recipient = new Recipient();
        recipient.setAccountName("test-quarterly");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.BACKUP, backup
        ));

        repository.save(recipient);

        List<Recipient> found = repository.findReadyForBackup();
        assertFalse(found.isEmpty());
    }
}