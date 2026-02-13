package com.piggymetrics.auth.service;

import com.piggymetrics.auth.domain.User;
import com.piggymetrics.auth.repository.UserRepository;
import org.junit.jupiter.api.Test; // JUnit 5
import org.junit.jupiter.api.extension.ExtendWith; // JUnit 5
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // JUnit 5 Mockito

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows; // JUnit 5 Exception testing
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Replaces initMocks(this) and @Before setup
class UserServiceTest { // Removed public

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository repository;

    @Test
    void shouldCreateUser() { // Removed public

        User user = new User();
        user.setUsername("name");
        user.setPassword("password");

        userService.create(user);
        verify(repository, times(1)).save(user);
    }

    @Test
    void shouldFailWhenUserAlreadyExists() {
        User user = new User();
        user.setUsername("name");
        user.setPassword("password");

        // Mock the repository to simulate an existing user
        when(repository.findById(user.getUsername())).thenReturn(Optional.of(new User()));

        // JUnit 5 way to verify an exception is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            userService.create(user);
        });
    }
}