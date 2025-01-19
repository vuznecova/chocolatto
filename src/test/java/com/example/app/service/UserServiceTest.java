package com.example.app.service;

import com.example.app.model.Role;
import com.example.app.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void testRegisterAndLogin() throws Exception {
        userService.register("testUser", "password", Role.CUSTOMER);
        User loggedInUser = userService.login("testUser", "password");
        assertNotNull(loggedInUser);
        assertEquals("testUser", loggedInUser.getLogin());
    }

    @Test
    void testRegisterDuplicateUser() {
        assertThrows(Exception.class, () -> {
            userService.register("duplicateUser", "password", Role.ADMIN);
            userService.register("duplicateUser", "password", Role.ADMIN);
        });
    }

    @Test
    void testInvalidLogin() {
        assertThrows(Exception.class, () -> userService.login("nonExistent", "password"));
    }
}