package com.example.app.service;

import com.example.app.model.Order;
import com.example.app.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private List<Order> orders;

    public OrderService() {
        orders = FileStorage.loadOrders();
    }

    public void createOrder(String userLogin, String address, String itemsDesc) {
        Order newOrder = new Order(
                userLogin,
                LocalDateTime.now(),
                OrderStatus.NEW,
                address,
                itemsDesc
        );
        orders.add(newOrder);
        FileStorage.saveOrders(orders);
    }

    public List<Order> getOrdersByUser(String userLogin) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.getUserLogin().equals(userLogin)) {
                result.add(o);
            }
        }
        return result;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        FileStorage.saveOrders(orders);
    }

    public void changeOrderStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        FileStorage.saveOrders(orders);
    }

    public void saveAllOrders() {
        FileStorage.saveOrders(orders);
    }

}
