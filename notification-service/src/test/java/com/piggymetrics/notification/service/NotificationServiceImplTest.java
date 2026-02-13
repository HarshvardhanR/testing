package com.piggymetrics.notification.service;

import com.google.common.collect.ImmutableList;
import com.piggymetrics.notification.client.AccountServiceClient;
import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import org.junit.jupiter.api.Test; // Updated to JUnit 5
import org.junit.jupiter.api.extension.ExtendWith; // Added
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // Added

import jakarta.mail.MessagingException; // Switched to jakarta
import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Handles mock initialization automatically
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private RecipientService recipientService;

    @Mock
    private AccountServiceClient client;

    @Mock
    private EmailService emailService;

    @Test
    void shouldSendBackupNotificationsEvenWhenErrorsOccursForSomeRecipients() throws IOException, MessagingException {

        final String attachment = "json";

        Recipient withError = new Recipient();
        withError.setAccountName("with-error");

        Recipient withNoError = new Recipient();
        withNoError.setAccountName("with-no-error");

        when(client.getAccount(withError.getAccountName())).thenThrow(new RuntimeException());
        when(client.getAccount(withNoError.getAccountName())).thenReturn(attachment);

        when(recipientService.findReadyToNotify(NotificationType.BACKUP)).thenReturn(ImmutableList.of(withNoError, withError));

        notificationService.sendBackupNotifications();

        // Verifying asynchronous behavior with timeout
        verify(emailService, timeout(100)).send(NotificationType.BACKUP, withNoError, attachment);
        verify(recipientService, timeout(100)).markNotified(NotificationType.BACKUP, withNoError);

        verify(recipientService, never()).markNotified(NotificationType.BACKUP, withError);
    }

    @Test
    void shouldSendRemindNotificationsEvenWhenErrorsOccursForSomeRecipients() throws IOException, MessagingException {

        Recipient withError = new Recipient();
        withError.setAccountName("with-error");

        Recipient withNoError = new Recipient();
        withNoError.setAccountName("with-no-error");

        when(recipientService.findReadyToNotify(NotificationType.REMIND)).thenReturn(ImmutableList.of(withNoError, withError));
        doThrow(new RuntimeException()).when(emailService).send(NotificationType.REMIND, withError, null);

        notificationService.sendRemindNotifications();

        verify(emailService, timeout(100)).send(NotificationType.REMIND, withNoError, null);
        verify(recipientService, timeout(100)).markNotified(NotificationType.REMIND, withNoError);

        verify(recipientService, never()).markNotified(NotificationType.REMIND, withError);
    }
}