package com.piggymetrics.auth.service.security;

import com.piggymetrics.auth.domain.User;
import com.piggymetrics.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Replaces initMocks(this) and @Before
class MongoUserDetailsServiceTest {

    @InjectMocks
    private MongoUserDetailsService service;

    @Mock
    private UserRepository repository;

    @Test
    void shouldLoadByUsernameWhenUserExists() {

        final User user = new User();
        user.setUsername("test-user");

        // Use anyString() instead of any() for clearer typing
        when(repository.findById(anyString())).thenReturn(Optional.of(user));
        
        UserDetails loaded = service.loadUserByUsername("test-user");

        // Compare the fields to avoid the type-mismatch error
        assertEquals(user.getUsername(), loaded.getUsername());
    }

    @Test
    void shouldFailToLoadByUsernameWhenUserNotExists() {
        // JUnit 5 style for testing exceptions
        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("name");
        });
    }
}