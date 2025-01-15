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

    private StackPane rootPane;   // Фон
    private VBox mainBox;         // Главный вертикальный контейнер

    // --- Заказы ---
    private TextArea ordersArea;
    private List<Order> allOrders;
    private TextField orderIndexField;
    private ComboBox<OrderStatus> statusCombo;

    // --- Товары ---
    private TextArea productsArea;

    public AdminController() {
        // 1) Фон + главный VBox
        rootPane = ViewUtils.createStackPaneWithBackground("/images/background1.jpg");
        mainBox = ViewUtils.createVBoxCenter(15, 20);
        rootPane.getChildren().add(mainBox);

        // -------------------- Блок ЗАКАЗОВ --------------------
        // Создаём HBox, чтобы слева положить текстовое поле, а справа кнопки
        HBox ordersBox = new HBox(15);
        ordersBox.setMaxWidth(900);          // ограничим ширину
        ordersBox.setStyle("-fx-border-color: gray; -fx-padding: 10;");
        ordersBox.setFillHeight(true);       // чтобы по высоте элементы растягивались

        // ЛЕВАЯ ЧАСТЬ (Vbox): заголовок и TextArea с заказами
        VBox ordersLeftBox = new VBox(10);
        Label header = new Label("Админ-панель: все заказы");

        ordersArea = new TextArea();
        ordersArea.setMaxWidth(400);
        ordersArea.setEditable(false);
        //ordersArea.setStyle("-fx-background-color: transparent;"); // можно отключить, если мешает скроллу

        ordersLeftBox.getChildren().addAll(header, ordersArea);

        // ПРАВАЯ ЧАСТЬ (Vbox): кнопки и поля
        VBox ordersRightBox = new VBox(10);

        Button refreshOrdersBtn = new Button("Обновить заказы");
        refreshOrdersBtn.setOnAction(e -> loadOrders());

        Label indexLabel = new Label("Введите номер заказа:");
        orderIndexField = new TextField();
        orderIndexField.setMaxWidth(150);

        Label statusLbl = new Label("Новый статус:");
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

        ordersRightBox.getChildren().addAll(
                refreshOrdersBtn,
                indexLabel, orderIndexField,
                statusLbl, statusCombo,
                setStatusBtn
        );

        // Добавляем левую и правую часть в HBox
        ordersBox.getChildren().addAll(ordersLeftBox, ordersRightBox);

        // Добавляем ordersBox в mainBox
        mainBox.getChildren().add(ordersBox);

        // --- Разделитель ---
        mainBox.getChildren().add(new Separator());

        // -------------------- Блок ТОВАРОВ --------------------
        HBox productsBox = new HBox(15);
        productsBox.setMaxWidth(900);
        productsBox.setStyle("-fx-border-color: gray; -fx-padding: 10;");
        productsBox.setFillHeight(true);

        // ЛЕВАЯ ЧАСТЬ: заголовок и TextArea
        VBox productsLeftBox = new VBox(10);
        Label productsLabel = new Label("Список товаров в магазине");

        productsArea = new TextArea();
        productsArea.setMaxWidth(400);
        productsArea.setEditable(false);
        //productsArea.setStyle("-fx-background-color: transparent;");

        productsLeftBox.getChildren().addAll(productsLabel, productsArea);

        // ПРАВАЯ ЧАСТЬ: кнопки
        VBox productsRightBox = new VBox(10);

        Button refreshProductsBtn = new Button("Обновить товары");
        refreshProductsBtn.setOnAction(e -> loadProducts());

        Button addProductBtn = new Button("Добавить товар");
        addProductBtn.setOnAction(e -> openAddProductDialog());

        Button removeProductBtn = new Button("Удалить товар");
        removeProductBtn.setOnAction(e -> openRemoveProductDialog());

        productsRightBox.getChildren().addAll(
                refreshProductsBtn,
                addProductBtn,
                removeProductBtn
        );

        productsBox.getChildren().addAll(productsLeftBox, productsRightBox);
        mainBox.getChildren().add(productsBox);

        // --- Ещё один разделитель ---
        mainBox.getChildren().add(new Separator());

        // -------------------- Кнопка «Выйти» в самом низу, по центру --------------------
        Button logoutBtn = new Button("Выйти");
        logoutBtn.setOnAction(e -> logout());
        mainBox.getChildren().add(logoutBtn);

        // После сборки структуры — загрузим списки
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
            loadProducts();
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
