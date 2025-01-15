package com.example.app.model;

public class Product {
    private String name;
    private double price;
    private String imagePath; // путь к файлу с изображением

    public Product(String name, double price, String imagePath) {
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String toString() {
        return name + " (" + price + ")";
    }
}
