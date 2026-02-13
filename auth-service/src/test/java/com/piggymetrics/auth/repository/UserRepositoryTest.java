package com.piggymetrics.auth.repository;

import com.piggymetrics.auth.domain.User;
import org.junit.jupiter.api.Test; // JUnit 5
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals; // JUnit 5 Assertions
import static org.junit.jupiter.api.Assertions.assertTrue;   // JUnit 5 Assertions

@DataMongoTest // This annotation already handles the Spring context and Mongo setup
class UserRepositoryTest { // Removed public

    @Autowired
    private UserRepository repository;

    @Test
    void shouldSaveAndFindUserByName() { // Removed public

        User user = new User();
        user.setUsername("name");
        user.setPassword("password");
        repository.save(user);

        Optional<User> found = repository.findById(user.getUsername());
        
        assertTrue(found.isPresent());
        assertEquals(user.getUsername(), found.get().getUsername());
        assertEquals(user.getPassword(), found.get().getPassword());
    }
}