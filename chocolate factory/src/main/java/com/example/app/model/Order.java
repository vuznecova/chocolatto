package com.example.app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Order {
    private String userLogin;       // Кто заказал
    private LocalDateTime dateTime; // Когда заказал
    private OrderStatus status;     // Статус

    public Order(String userLogin, LocalDateTime dateTime, OrderStatus status) {
        this.userLogin = userLogin;
        this.dateTime = dateTime;
        this.status = status;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    // Преобразуем в строку для записи в файл
    // Формат: userLogin;2025-01-01T10:30:00;NEW
    @Override
    public String toString() {
        return userLogin + ";"
                + dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                + ";" + status;
    }

    // Парсим из строки (CSV)
    public static Order fromString(String line) {
        String[] parts = line.split(";");
        if (parts.length < 3) return null;
        String user = parts[0];
        LocalDateTime dt = LocalDateTime.parse(parts[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        OrderStatus st = OrderStatus.valueOf(parts[2]);
        return new Order(user, dt, st);
    }
}
