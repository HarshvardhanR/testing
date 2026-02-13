package com.piggymetrics.notification.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.piggymetrics.notification.domain.Frequency;
import com.piggymetrics.notification.domain.NotificationSettings;
import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import com.piggymetrics.notification.repository.RecipientRepository;
import org.junit.jupiter.api.Test; // Updated
import org.junit.jupiter.api.extension.ExtendWith; // Added
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // Added

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*; // Updated
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Replaces initMocks(this)
class RecipientServiceImplTest {

    @InjectMocks
    private RecipientServiceImpl recipientService;

    @Mock
    private RecipientRepository repository;

    @Test
    void shouldFindByAccountName() {
        Recipient recipient = new Recipient();
        recipient.setAccountName("test");

        when(repository.findByAccountName(recipient.getAccountName())).thenReturn(recipient);
        Recipient found = recipientService.findByAccountName(recipient.getAccountName());

        assertEquals(recipient, found);
    }

    @Test
    void shouldFailToFindRecipientWhenAccountNameIsEmpty() {
        // Updated to JUnit 5 assertThrows style
        assertThrows(IllegalArgumentException.class, () -> {
            recipientService.findByAccountName("");
        });
    }

    @Test
    void shouldSaveRecipient() {

        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(null);

        NotificationSettings backup = new NotificationSettings();
        backup.setActive(false);
        backup.setFrequency(Frequency.MONTHLY);
        backup.setLastNotified(new Date());

        Recipient recipient = new Recipient();
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.BACKUP, backup,
                NotificationType.REMIND, remind
        ));

        Recipient saved = recipientService.save("test", recipient);

        verify(repository).save(recipient);
        assertNotNull(saved.getScheduledNotifications().get(NotificationType.REMIND).getLastNotified());
        assertEquals("test", saved.getAccountName());
    }

    @Test
    void shouldFindReadyToNotifyWhenNotificationTypeIsBackup() {
        final List<Recipient> recipients = ImmutableList.of(new Recipient());
        when(repository.findReadyForBackup()).thenReturn(recipients);

        List<Recipient> found = recipientService.findReadyToNotify(NotificationType.BACKUP);
        assertEquals(recipients, found);
    }

    @Test
    void shouldFindReadyToNotifyWhenNotificationTypeIsRemind() {
        final List<Recipient> recipients = ImmutableList.of(new Recipient());
        when(repository.findReadyForRemind()).thenReturn(recipients);

        List<Recipient> found = recipientService.findReadyToNotify(NotificationType.REMIND);
        assertEquals(recipients, found);
    }

    @Test
    void shouldMarkAsNotified() {

        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(null);

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.REMIND, remind
        ));

        recipientService.markNotified(NotificationType.REMIND, recipient);
        assertNotNull(recipient.getScheduledNotifications().get(NotificationType.REMIND).getLastNotified());
        verify(repository).save(recipient);
    }
}