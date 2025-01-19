package com.example.app.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserToString() {
        User user = new User("user1", "password1", Role.CUSTOMER);
        String expected = "user1;password1;CUSTOMER";
        assertEquals(expected, user.toString());
    }

    @Test
    void testUserFromString() {
        String userStr = "user1;password1;CUSTOMER";
        User user = User.fromString(userStr);

        assertNotNull(user);
        assertEquals("user1", user.getLogin());
        assertEquals("password1", user.getPassword());
        assertEquals(Role.CUSTOMER, user.getRole());
    }

    @Test
    void testInvalidUserFromString() {
        String invalidStr = "user1;password1";
        User user = User.fromString(invalidStr);
        assertNull(user);
    }
}