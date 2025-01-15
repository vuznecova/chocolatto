package com.example.app.controller;

import com.example.MainApp.java.MainApp;
import com.example.app.model.Order;
import com.example.app.model.OrderStatus;
import com.example.app.model.Product;
import com.example.app.utils.ViewUtils;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class AdminController {
    private StackPane rootPane;
    private VBox mainBox;

    // Показ списка заказов
    private TextArea ordersArea;
    private List<Order> allOrders;

    // Показ списка товаров
    private TextArea productsArea;

    // Для изменения статуса заказов
    private TextField orderIndexField;
    private ComboBox<OrderStatus> statusCombo;

    public AdminController() {
        // 1) Создаём фон и основной VBox
        rootPane = ViewUtils.createStackPaneWithBackground("/images/background.jpg");
        mainBox = ViewUtils.createVBoxCenter(15, 20);
        rootPane.getChildren().add(mainBox);

        // ============ Блок для заказов ============
        Label header = new Label("Админ-панель: все заказы");
        ordersArea = new TextArea();
        ordersArea.setMaxWidth(900);
        ordersArea.setEditable(false);

        Button refreshOrdersBtn = new Button("Обновить заказы");
        refreshOrdersBtn.setOnAction(e -> loadOrders());

        Label indexLabel = new Label("Введите номер заказа:");
        orderIndexField = new TextField();
        orderIndexField.setMaxWidth(200);

        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(
                OrderStatus.NEW,
                OrderStatus.IN_PROGRESS,
                OrderStatus.COMPLETED,
                OrderStatus.CANCELED
        );
        statusCombo.setValue(OrderStatus.NEW);

        Button setStatusBtn = new Button("Изменить статус");
        setStatusBtn.setOnAction(e -> changeStatus());

        // ============ Блок для товаров ============
        Label productsLabel = new Label("Список товаров в магазине");
        productsArea = new TextArea();
        productsArea.setMaxWidth(900);
        productsArea.setEditable(false);

        Button refreshProductsBtn = new Button("Обновить товары");
        refreshProductsBtn.setOnAction(e -> loadProducts());

        Button addProductBtn = new Button("Добавить товар");
        addProductBtn.setOnAction(e -> openAddProductDialog());

        Button removeProductBtn = new Button("Удалить товар");
        removeProductBtn.setOnAction(e -> openRemoveProductDialog());

        // ============ Кнопка выхода ============
        Button logoutBtn = new Button("Выйти");
        logoutBtn.setOnAction(e -> logout());

        // Добавляем всё в mainBox
        mainBox.getChildren().addAll(
                header, ordersArea, refreshOrdersBtn,
                indexLabel, orderIndexField,
                new Label("Новый статус:"), statusCombo, setStatusBtn,

                new Separator(),

                productsLabel, productsArea, refreshProductsBtn,
                addProductBtn, removeProductBtn,

                new Separator(),
                logoutBtn
        );

        // При инициализации сразу подгружаем списки
        loadOrders();
        loadProducts();
    }

    public StackPane getView() {
        return rootPane;
    }

    // ============================================
    // Методы для заказов
    // ============================================

    private void loadOrders() {
        allOrders = MainApp.getOrderService().getAllOrders();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allOrders.size(); i++) {
            Order o = allOrders.get(i);
            sb.append(i).append(") ")
                    .append("Пользователь: ").append(o.getUserLogin()).append(" | ")
                    .append("Дата: ").append(o.getDateTime()).append(" | ")
                    .append("Статус: ").append(o.getStatus()).append(" | ")
                    .append("Адрес: ").append(o.getAddress())
                    .append("\n");
        }
        if (allOrders.isEmpty()) {
            sb.append("Пока нет заказов");
        }
        ordersArea.setText(sb.toString());
    }

    private void changeStatus() {
        try {
            int index = Integer.parseInt(orderIndexField.getText());
            if (index < 0 || index >= allOrders.size()) {
                new Alert(Alert.AlertType.ERROR, "Неверный индекс!").showAndWait();
                return;
            }
            Order o = allOrders.get(index);
            OrderStatus newStatus = statusCombo.getValue();
            MainApp.getOrderService().changeOrderStatus(o, newStatus);
            new Alert(Alert.AlertType.INFORMATION,
                    "Статус заказа изменён на " + newStatus).showAndWait();
            loadOrders();
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Введите корректный номер заказа!").showAndWait();
        }
    }

    // ============================================
    // Методы для товаров
    // ============================================

    private void loadProducts() {
        List<Product> allProducts = MainApp.getProductService().getAllProducts();
        if (allProducts.isEmpty()) {
            productsArea.setText("Нет товаров");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allProducts.size(); i++) {
            Product p = allProducts.get(i);
            sb.append(i).append(") ")
                    .append(p.getName()).append(" - ")
                    .append(p.getPrice()).append(" $");
            if (p.getImagePath() != null && !p.getImagePath().isEmpty()) {
                sb.append(" | img=").append(p.getImagePath());
            }
            sb.append("\n");
        }
        productsArea.setText(sb.toString());
    }

    private void openAddProductDialog() {
        AddProductDialogController dialog = new AddProductDialogController(
                (Stage) rootPane.getScene().getWindow()
        );
        dialog.showDialog();

        if (dialog.isOkClicked()) {
            String name = dialog.getProductName();
            String priceStr = dialog.getPrice();
            String imagePath = dialog.getImagePath();

            if (name.isEmpty() || priceStr.isEmpty()) {
                new Alert(Alert.AlertType.WARNING,
                        "Название и цена не могут быть пустыми!")
                        .showAndWait();
                return;
            }
            double priceVal;
            try {
                priceVal = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.WARNING,
                        "Цена должна быть числом!")
                        .showAndWait();
                return;
            }
            MainApp.getProductService().addProduct(name, priceVal, imagePath);

            new Alert(Alert.AlertType.INFORMATION,
                    "Товар успешно добавлен!").showAndWait();
            loadProducts(); // обновим список
        }
    }

    private void openRemoveProductDialog() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setHeaderText("Введите индекс товара для удаления:");
        var result = inputDialog.showAndWait();
        if (result.isEmpty()) return;

        String indexStr = result.get();
        int index;
        try {
            index = Integer.parseInt(indexStr);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Неверный формат индекса!").showAndWait();
            return;
        }

        List<Product> allProducts = MainApp.getProductService().getAllProducts();
        if (index < 0 || index >= allProducts.size()) {
            new Alert(Alert.AlertType.ERROR, "Такого индекса нет в списке!").showAndWait();
            return;
        }

        MainApp.getProductService().removeProduct(index);
        new Alert(Alert.AlertType.INFORMATION, "Товар удалён!").showAndWait();
        loadProducts();
    }

    // ============================================
    // Выход
    // ============================================

    private void logout() {
        LoginController loginController = new LoginController();
        rootPane.getScene().setRoot(loginController.getView());
    }
}
