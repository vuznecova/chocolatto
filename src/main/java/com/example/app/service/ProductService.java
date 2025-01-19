package com.example.app.service;

import com.example.app.model.Product;
import java.util.List;

public class ProductService {
    private List<Product> products;

    public ProductService() {
        // Сначала загружаем товары из файла (если они там есть)
        products = FileStorage.loadProducts();

        // Если нужно, чтобы при первом запуске (когда файл ещё пустой)
        // было вообще 0 товаров, то ничего не добавляем по умолчанию.
        // Таким образом, только те товары, что админ добавит, сохранятся
        // и при повторном запуске снова будут загружены из файла.
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public void addProduct(String name, double price, String imagePath) {
        // Создаём новый товар и добавляем в список
        Product newP = new Product(name, price, imagePath);
        products.add(newP);
        // Сохраняем обновлённый список в файл
        FileStorage.saveProducts(products);
    }

    public void removeProduct(int index) {
        products.remove(index);
        FileStorage.saveProducts(products);
    }
}
