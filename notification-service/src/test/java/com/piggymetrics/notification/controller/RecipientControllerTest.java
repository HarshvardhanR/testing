package com.piggymetrics.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.piggymetrics.notification.domain.Frequency;
import com.piggymetrics.notification.domain.NotificationSettings;
import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import com.piggymetrics.notification.service.RecipientService;
import org.junit.jupiter.api.BeforeEach; // Updated
import org.junit.jupiter.api.Test; // Updated
import org.junit.jupiter.api.extension.ExtendWith; // Added
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // Added
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal; // Standard Java Security

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class) // Modern Mockito integration
class RecipientControllerTest { // Package-private is fine in JUnit 5

    private static final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private RecipientController recipientController;

    @Mock
    private RecipientService recipientService;

    private MockMvc mockMvc;

    @BeforeEach // Replaces @Before
    void setup() {
        // initMocks is handled by @ExtendWith(MockitoExtension.class)
        this.mockMvc = MockMvcBuilders.standaloneSetup(recipientController).build();
    }

    @Test
    void shouldSaveCurrentRecipientSettings() throws Exception {

        Recipient recipient = getStubRecipient();
        String json = mapper.writeValueAsString(recipient);

        // Replaced UserPrincipal with a lambda for Principal
        mockMvc.perform(put("/recipients/current")
                .principal(() -> recipient.getAccountName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetCurrentRecipientSettings() throws Exception {

        Recipient recipient = getStubRecipient();
        when(recipientService.findByAccountName(recipient.getAccountName())).thenReturn(recipient);

        mockMvc.perform(get("/recipients/current")
                .principal(() -> recipient.getAccountName()))
                .andExpect(jsonPath("$.accountName").value(recipient.getAccountName()))
                .andExpect(status().isOk());
    }

    private Recipient getStubRecipient() {

        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(null);

        NotificationSettings backup = new NotificationSettings();
        backup.setActive(false);
        backup.setFrequency(Frequency.MONTHLY);
        backup.setLastNotified(null);

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.BACKUP, backup,
                NotificationType.REMIND, remind
        ));

        return recipient;
    }
}