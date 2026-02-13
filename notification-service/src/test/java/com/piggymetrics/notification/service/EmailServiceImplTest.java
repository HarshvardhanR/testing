package com.piggymetrics.notification.service;

import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import org.junit.jupiter.api.BeforeEach; // Updated
import org.junit.jupiter.api.Test; // Updated
import org.junit.jupiter.api.extension.ExtendWith; // Added
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // Added
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.MessagingException; // Switched to jakarta
import jakarta.mail.Session;           // Switched to jakarta
import jakarta.mail.internet.MimeMessage; // Switched to jakarta
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals; // Updated
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private Environment env;

    @Captor
    private ArgumentCaptor<MimeMessage> captor;

    @BeforeEach
    void setup() {
        // Mocking the creation of MimeMessage using the Jakarta Session
        when(mailSender.createMimeMessage())
                .thenReturn(new MimeMessage(Session.getDefaultInstance(new Properties())));
    }

    @Test
    void shouldSendBackupEmail() throws MessagingException, IOException {

        final String subject = "subject";
        final String text = "text";
        final String attachment = "attachment.json";

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");

        when(env.getProperty(NotificationType.BACKUP.getSubject())).thenReturn(subject);
        when(env.getProperty(NotificationType.BACKUP.getText())).thenReturn(text);
        when(env.getProperty(NotificationType.BACKUP.getAttachment())).thenReturn(attachment);

        emailService.send(NotificationType.BACKUP, recipient, "{\"name\":\"test\"");

        verify(mailSender).send(captor.capture());

        MimeMessage message = captor.getValue();
        assertEquals(subject, message.getSubject());
    }

    @Test
    void shouldSendRemindEmail() throws MessagingException, IOException {

        final String subject = "subject";
        final String text = "text";

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");

        when(env.getProperty(NotificationType.REMIND.getSubject())).thenReturn(subject);
        when(env.getProperty(NotificationType.REMIND.getText())).thenReturn(text);

        emailService.send(NotificationType.REMIND, recipient, null);

        verify(mailSender).send(captor.capture());

        MimeMessage message = captor.getValue();
        assertEquals(subject, message.getSubject());
    }
}