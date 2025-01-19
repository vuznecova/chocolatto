package com.example.app.service;

import com.example.app.model.Order;
import com.example.app.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    @Test
    void testCreateOrder() {
        orderService.createOrder("user1", "123 Street", "Item1");
        List<Order> orders = orderService.getOrdersByUser("user1");
        assertFalse(orders.isEmpty());
        assertEquals("123 Street", orders.get(0).getAddress());
    }

    @Test
    void testChangeOrderStatus() {
        orderService.createOrder("user1", "123 Street", "Item1");
        Order order = orderService.getOrdersByUser("user1").get(0);
        orderService.changeOrderStatus(order, OrderStatus.COMPLETED);
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }
}