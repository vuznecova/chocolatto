package com.example.app.service;

import com.example.app.model.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private List<Product> products;

    public ProductService() {
        products = FileStorage.loadProducts();

        products = new ArrayList<>();
        // Можно добавить изначальные товары
        products.add(new Product("Молочный шоколад", 5.0, "/images/Milk.png"));
        products.add(new Product("Тёмный шоколад", 6.0, "/images/dark_choko.png"));
    }

    public List<Product> getAllProducts() {

        return products;
    }

    public void addProduct(String name, double price, String imagePath) {
        Product newP = new Product(name, price, imagePath);
        products.add(newP);
        // Сохраняем в файл
        FileStorage.saveProducts(products);
    }
    public void removeProduct(int index) {
        products.remove(index);
        FileStorage.saveProducts(products);
    }

}


