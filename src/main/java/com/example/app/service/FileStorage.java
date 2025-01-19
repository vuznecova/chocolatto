package com.example.app.service;

import com.example.app.model.Order;
import com.example.app.model.Product;
import com.example.app.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {

    private static final String USERS_FILE = "users.txt";
    private static final String ORDERS_FILE = "orders.txt";
    public static final String PRODUCTS_FILE = "products.txt";

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                User u = User.fromString(line);
                if (u != null) users.add(u);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static void saveUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) {
                bw.write(u.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Order> loadOrders() {
        List<Order> orders = new ArrayList<>();
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return orders;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Order o = Order.fromString(line);
                if (o != null) {
                    orders.add(o);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static void saveOrders(List<Order> orders) {
        File file = new File(ORDERS_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Order o : orders) {
                String line = o.toString();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return products;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Product p = productFromString(line);
                if (p != null) {
                    products.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    private static Product productFromString(String line) {
        String[] parts = line.split(";");
        if (parts.length < 2) return null;
        String name = parts[0];
        double price = Double.parseDouble(parts[1]);
        String imagePath = (parts.length > 2) ? parts[2] : "";
        return new Product(name, price, imagePath);
    }

    public static void saveProducts(List<Product> products) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product p : products) {
                bw.write(productToString(p));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String productToString(Product p) {
        return p.getName() + ";" + p.getPrice() + ";"
                + ((p.getImagePath() == null) ? "" : p.getImagePath());
    }
}
