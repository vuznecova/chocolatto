package com.example.app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {
    private String userLogin;
    private LocalDateTime dateTime;
    private OrderStatus status;

    // Новое поле для хранения адреса доставки
    private String address;

    public Order(String userLogin, LocalDateTime dateTime, OrderStatus status, String address) {
        this.userLogin = userLogin;
        this.dateTime = dateTime;
        this.status = status;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Преобразуем в CSV-строку с учётом адреса
    @Override
    public String toString() {
        // Формат: userLogin;2025-01-01T10:30:00;NEW;адрес
        return userLogin + ";"
                + dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ";"
                + status + ";"
                + (address == null ? "" : address.replace(";", ","));
        // Заменяем ; внутри адреса на запятую, чтобы не ломать CSV
    }

    // Парсим из строки
    public static Order fromString(String line) {
        String[] parts = line.split(";");
        if (parts.length < 3) return null;
        String user = parts[0];
        LocalDateTime dt = LocalDateTime.parse(parts[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        OrderStatus st = OrderStatus.valueOf(parts[2]);
        String addr = "";
        if (parts.length > 3) {
            addr = parts[3].replace(",", ";");
            // Возвращаем точку с запятой, если заменяли
        }
        return new Order(user, dt, st, addr);
    }
}
