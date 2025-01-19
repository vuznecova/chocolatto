package com.example.app.service;

import com.example.app.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
    }

    @Test
    void testAddProduct() {
        int ProductCount = productService.getAllProducts().size();
        productService.addProduct("Chocolate", 10.5, "/images/choco.png");
        List<Product> products = productService.getAllProducts();
        assertFalse(products.isEmpty());
        assertEquals("Chocolate", products.get(ProductCount).getName());
    }

    @Test
    void testRemoveProduct() {
        int previousProductCount = productService.getAllProducts().size();
        productService.addProduct("Chocolate", 10.5, "/images/choco.png");
        productService.removeProduct(previousProductCount);
        int productCount = productService.getAllProducts().size();
        assertEquals(previousProductCount, productCount);
    }
}