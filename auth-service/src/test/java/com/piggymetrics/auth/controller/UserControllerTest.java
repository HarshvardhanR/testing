package com.piggymetrics.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggymetrics.auth.domain.User;
import com.piggymetrics.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach; // JUnit 5
import org.junit.jupiter.api.Test;       // JUnit 5
import org.junit.jupiter.api.extension.ExtendWith; // JUnit 5
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // JUnit 5 Mockito
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class) // Replaces @RunWith(SpringRunner.class) and initMocks
class UserControllerTest { // Removed public

    private static final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private UserController accountController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach // Replaces @Before
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void shouldCreateNewUser() throws Exception {
        final User user = new User();
        user.setUsername("test");
        user.setPassword("password");

        String json = mapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailWhenUserIsNotValid() throws Exception {
        // In modern Spring, we usually need to send a body to trigger validation 
        // or just send empty JSON if the object is required.
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")) 
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnCurrentUser() throws Exception {
        // Using a lambda for Principal instead of the internal com.sun class
        Principal principal = () -> "test"; 

        mockMvc.perform(get("/users/current")
                .principal(principal))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(status().isOk());
    }
}