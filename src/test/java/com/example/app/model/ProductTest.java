package com.example.app.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testProductToString() {
        Product product = new Product("Chocolate", 10.0, "/images/choco.png");
        String expected = "Chocolate (10.0)";
        assertEquals(expected, product.toString());
    }

    @Test
    void testProductFields() {
        Product product = new Product("Candy", 5.0, "/images/candy.png");

        assertEquals("Candy", product.getName());
        assertEquals(5.0, product.getPrice());
        assertEquals("/images/candy.png", product.getImagePath());
    }
}