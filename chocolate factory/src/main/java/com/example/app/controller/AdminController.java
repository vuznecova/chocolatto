package com.example.app.controller;

import com.example.MainApp.java.MainApp;
import com.example.app.model.Order;
import com.example.app.model.OrderStatus;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.List;

public class AdminController {
    private VBox view;
    private TextArea ordersArea;
    private ComboBox<OrderStatus> statusCombo;
    private TextField orderIndexField;

    private List<Order> allOrders;

    public AdminController() {
        view = new VBox(10);
        view.setPadding(new Insets(10));

        Label header = new Label("Админ-панель: все заказы");
        ordersArea = new TextArea();
        ordersArea.setEditable(false);

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setOnAction(e -> loadOrders());

        Label indexLabel = new Label("Введите номер заказа (по списку выше) для смены статуса:");
        orderIndexField = new TextField();

        Label statusLabel = new Label("Новый статус:");
        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(OrderStatus.NEW, OrderStatus.IN_PROGRESS,
                OrderStatus.COMPLETED, OrderStatus.CANCELED);
        statusCombo.setValue(OrderStatus.NEW);

        Button setStatusBtn = new Button("Изменить статус");
        setStatusBtn.setOnAction(e -> changeStatus());

        Button logoutBtn = new Button("Выйти");
        logoutBtn.setOnAction(e -> logout());

        view.getChildren().addAll(
                header, ordersArea, refreshBtn,
                indexLabel, orderIndexField,
                statusLabel, statusCombo,
                setStatusBtn, logoutBtn
        );

        loadOrders(); // Загрузим при инициализации
    }

    public VBox getView() {
        return view;
    }

    private void loadOrders() {
        allOrders = MainApp.getOrderService().getAllOrders();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allOrders.size(); i++) {
            Order o = allOrders.get(i);
            sb.append(i).append(") ")
                    .append(o.getUserLogin()).append(" - ")
                    .append(o.getDateTime()).append(" - ")
                    .append(o.getStatus()).append("\n");
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
            new Alert(Alert.AlertType.INFORMATION, "Статус заказа изменён на " + newStatus).showAndWait();
            loadOrders(); // Обновить список
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Введите корректный номер заказа!").showAndWait();
        }
    }

    private void logout() {
        LoginController loginController = new LoginController();
        view.getScene().setRoot(loginController.getView());
    }
}

