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

    // Создание нового заказа
    public void createOrder(String userLogin, String address) {
        Order newOrder = new Order(userLogin, LocalDateTime.now(), OrderStatus.NEW, address);
        orders.add(newOrder);
        FileStorage.saveOrders(orders); // если вы храните заказы в файле
    }


    // Получить заказы конкретного пользователя
    public List<Order> getOrdersByUser(String userLogin) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if (o.getUserLogin().equals(userLogin)) {
                result.add(o);
            }
        }
        return result;
    }

    // Получить все заказы (для администратора)
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        FileStorage.saveOrders(orders); // если вы храните заказы в файле
    }


    // Изменить статус заказа
    public void changeOrderStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        FileStorage.saveOrders(orders);
    }
}

