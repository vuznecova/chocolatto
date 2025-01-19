package com.example.app.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testOrderToString() {
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order("user1", now, OrderStatus.NEW, "123 Street", "Item1");

        String serialized = order.toString();
        assertTrue(serialized.contains("user1"));
        assertTrue(serialized.contains("NEW"));
    }

    @Test
    void testOrderFromString() {
        String orderStr = "user1;2025-01-01T10:00:00;NEW;123 Street;Item1";
        Order order = Order.fromString(orderStr);

        assertNotNull(order);
        assertEquals("user1", order.getUserLogin());
        assertEquals(OrderStatus.NEW, order.getStatus());
        assertEquals("123 Street", order.getAddress());
    }

    @Test
    void testInvalidOrderFromString() {
        String invalidOrderStr = "user1;invalid-date;NEW;123 Street;Item1";
        Order order = Order.fromString(invalidOrderStr);
        assertNull(order);
    }
}