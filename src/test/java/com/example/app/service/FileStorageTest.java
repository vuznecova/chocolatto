package com.example.app.service;

import com.example.app.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageTest {

    private File tempUsersFile;
    private File tempOrdersFile;
    private File tempProductsFile;
    public static String PRODUCTS_FILE = "products.txt";
    @BeforeEach
    void setUp() throws IOException {
        tempUsersFile = File.createTempFile("users", ".txt");
        tempOrdersFile = File.createTempFile("orders", ".txt");
        tempProductsFile = File.createTempFile("products", ".txt");

        String USERS_FILE = tempUsersFile.getAbsolutePath();
        String ORDERS_FILE = tempOrdersFile.getAbsolutePath();
        PRODUCTS_FILE = tempProductsFile.getAbsolutePath();
    }

    @AfterEach
    void tearDown() {
        tempUsersFile.delete();
        tempOrdersFile.delete();
        tempProductsFile.delete();
    }

    @Test
    void testSaveAndLoadUsers() {
        List<User> users = List.of(
                new User("user1", "password1", Role.CUSTOMER),
                new User("admin", "adminpass", Role.ADMIN)
        );

        FileStorage.saveUsers(users);
        List<User> loadedUsers = FileStorage.loadUsers();

        assertEquals(users.size(), loadedUsers.size());
        assertEquals(users.get(0).getLogin(), loadedUsers.get(0).getLogin());
    }

    @Test
    void testSaveAndLoadOrders() {
        List<Order> orders = List.of(
                new Order("user1", LocalDateTime.now(), OrderStatus.NEW, "123 Street", "Item1"),
                new Order("user2", LocalDateTime.now(), OrderStatus.COMPLETED, "456 Avenue", "Item2")
        );

        FileStorage.saveOrders(orders);
        List<Order> loadedOrders = FileStorage.loadOrders();

        assertEquals(orders.size(), loadedOrders.size());
        assertEquals(orders.get(0).getUserLogin(), loadedOrders.get(0).getUserLogin());
    }

    @Test
    void testSaveAndLoadProducts() {
        List<Product> products = List.of(
                new Product("Chocolate", 10.0, "/images/choco.png"),
                new Product("Candy", 5.0, "/images/candy.png")
        );

        FileStorage.saveProducts(products);
        List<Product> loadedProducts = FileStorage.loadProducts();

        assertEquals(products.size(), loadedProducts.size());
        assertEquals(products.get(0).getName(), loadedProducts.get(0).getName());
    }
}