package com.example.app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {
    private String userLogin;
    private LocalDateTime dateTime;
    private OrderStatus status;

    // Новое поле для хранения адреса доставки
    private String address;
    private String itemsDesc;

    public Order(String userLogin, LocalDateTime dateTime, OrderStatus status, String address, String itemsDesc){
        this.userLogin = userLogin;
        this.dateTime = dateTime;
        this.status = status;
        this.address = address;
        this.itemsDesc = itemsDesc;
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

    public String getItemsDesc() {
        return itemsDesc;
    }

    // Преобразуем в CSV-строку с учётом адреса
    @Override
    public String toString() {
        // Форматируем LocalDateTime в строку
        String dtStr = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        // Пишем поля через «;»
        // Заменим «;» внутри address/itemsDesc на запятую, если боимся конфликтов
        String safeAddress = (address == null) ? "" : address.replace(";", ",");
        String safeItems = (itemsDesc == null) ? "" : itemsDesc.replace(";", ",");
        // Итог: user;2023-12-20T10:12;NEW;адрес;items
        return userLogin + ";"
                + dtStr + ";"
                + status + ";"
                + safeAddress + ";"
                + safeItems;
    }

    // Десериализация (чтение из файла)
    public static Order fromString(String line) {
        // Разбиваем по ";"
        String[] parts = line.split(";");
        if (parts.length < 3) {
            // Если меньше 3 полей, точно что-то не так
            return null;
        }
        String user = parts[0];
        // Парсим дату/время
        LocalDateTime dt;
        try {
            dt = LocalDateTime.parse(parts[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            // Если вдруг дата не парсится — вернётся null
            return null;
        }
        OrderStatus st;
        try {
            st = OrderStatus.valueOf(parts[2]);
        } catch (Exception e) {
            return null;
        }
        // address и itemsDesc могут быть пустыми
        String addr = (parts.length > 3) ? parts[3].replace(",", ";") : "";
        String items = (parts.length > 4) ? parts[4].replace(",", ";") : "";

        return new Order(user, dt, st, addr, items);
    }
}
