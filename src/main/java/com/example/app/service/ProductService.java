package com.example.app.service;

import com.example.app.model.Product;
import java.util.List;

public class ProductService {
    private List<Product> products;

    public ProductService() {
        products = FileStorage.loadProducts();
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public void addProduct(String name, double price, String imagePath) {
        Product newP = new Product(name, price, imagePath);
        products.add(newP);
        FileStorage.saveProducts(products);
    }

    public void removeProduct(int index) {
        products.remove(index);
        FileStorage.saveProducts(products);
    }
}
