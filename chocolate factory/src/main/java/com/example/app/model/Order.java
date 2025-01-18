package com.example.app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {
    private String userLogin;
    private LocalDateTime dateTime;
    private OrderStatus status;
    private String address;
    private String itemsDesc;

    public Order(String userLogin, LocalDateTime dateTime,
                 OrderStatus status, String address, String itemsDesc) {
        this.userLogin = userLogin;
        this.dateTime = dateTime;
        this.status = status;
        this.address = address;
        this.itemsDesc = itemsDesc;
    }

    public String getUserLogin() { return userLogin; }
    public LocalDateTime getDateTime() { return dateTime; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus s) { this.status = s; }
    public String getAddress() { return address; }
    public String getItemsDesc() { return itemsDesc; }

    // Сериализация (запись в одну CSV-строку)
    @Override
    public String toString() {
        // Форматируем LocalDateTime в ISO-строку
        String dtStr = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        // Заменяем ; внутри address/itemsDesc, если боимся конфликтов
        String safeAddress = (address == null) ? "" : address.replace(";", ",");
        String safeItems  = (itemsDesc == null) ? "" : itemsDesc.replace(";", ",");

        // user;2025-01-23T17:25:00;NEW;safeAddress;safeItems
        return userLogin + ";"
                + dtStr + ";"
                + status + ";"
                + safeAddress + ";"
                + safeItems;
    }

    // Десериализация (чтение строки из CSV)
    public static Order fromString(String line) {
        // Разбиваем
        String[] parts = line.split(";");
        if (parts.length < 3) {
            // Минимум 3 поля (логин, дата, статус).
            return null;
        }

        String user = parts[0];
        LocalDateTime dt;
        try {
            dt = LocalDateTime.parse(parts[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            System.out.println("Ошибка парсинга даты: " + parts[1]);
            return null;
        }
        OrderStatus st;
        try {
            st = OrderStatus.valueOf(parts[2]);
        } catch (Exception e) {
            System.out.println("Ошибка парсинга статуса: " + parts[2]);
            return null;
        }
        String addr = (parts.length > 3) ? parts[3].replace(",", ";") : "";
        String items = (parts.length > 4) ? parts[4].replace(",", ";") : "";

        return new Order(user, dt, st, addr, items);
    }
}
