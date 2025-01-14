package com.example.app.controller;

import com.example.MainApp.java.MainApp;
import com.example.app.model.Order;
import com.example.app.model.Product;
import com.example.app.model.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class ShopController {
    private VBox view;
    private Label cartLabel;
    private User currentUser;

    private List<String> cart;  // Упрощённо: список названий товаров

    public ShopController(User user) {
        this.currentUser = user;
        this.cart = new ArrayList<>();

        view = new VBox(10);
        view.setPadding(new Insets(10));

        Label welcomeLabel = new Label("Добро пожаловать, " + currentUser.getLogin() + "!");
        view.getChildren().add(welcomeLabel);

        // Демонстрационно добавим пару "товаров"
        addProductItem("Молочный шоколад", 5.0);
        addProductItem("Тёмный шоколад", 6.0);

        cartLabel = new Label("Корзина пуста");
        Button orderBtn = new Button("Оформить заказ");
        Button myOrdersBtn = new Button("Мои заказы");
        Button logoutBtn = new Button("Выйти");

        orderBtn.setOnAction(e -> createOrder());
        myOrdersBtn.setOnAction(e -> showMyOrders());
        logoutBtn.setOnAction(e -> logout());

        view.getChildren().addAll(cartLabel, orderBtn, myOrdersBtn, logoutBtn);
    }

    public VBox getView() {
        return view;
    }

    private void addProductItem(String productName, double price) {
        Button addBtn = new Button("В корзину");
        Label productLabel = new Label(productName + " - " + price + "$");
        addBtn.setOnAction(e -> {
            cart.add(productName);
            cartLabel.setText("В корзине: " + cart.size() + " товар(ов)");
        });

        VBox productBox = new VBox(5, productLabel, addBtn);
        productBox.setStyle("-fx-border-color: gray; -fx-padding: 5;");
        view.getChildren().add(productBox);
    }

    private void createOrder() {
        if (cart.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Корзина пуста!").showAndWait();
            return;
        }

        // Открываем диалог для ввода адреса
        AddressDialogController dialog = new AddressDialogController(
                (Stage) view.getScene().getWindow()
        );
        dialog.showDialog();

        String address = dialog.getAddressResult();
        if (address == null) {
            // Пользователь нажал "Отмена" или не ввёл адрес
            new Alert(Alert.AlertType.INFORMATION,
                    "Заказ не был оформлен без адреса.").showAndWait();
            return;
        }

        // Если адрес получен, создаём заказ
        // (В реальном проекте нужно где-то хранить этот address,
        //  либо добавлять в класс Order, например).
        MainApp.getOrderService().createOrder(currentUser.getLogin());
        // Можно логировать/сохранять address куда-то или
        // расширить метод createOrder(…address) — на ваше усмотрение

        cart.clear();
        cartLabel.setText("Корзина пуста");
        new Alert(Alert.AlertType.INFORMATION,
                "Заказ оформлен!\nАдрес: " + address).showAndWait();
    }

    private void showMyOrders() {
        var myOrders = MainApp.getOrderService().getOrdersByUser(currentUser.getLogin());
        StringBuilder sb = new StringBuilder("Мои заказы:\n");
        for (var o : myOrders) {
            sb.append(o.getDateTime()).append(" - ").append(o.getStatus()).append("\n");
        }
        new Alert(Alert.AlertType.INFORMATION, sb.toString()).showAndWait();
    }

    private void logout() {
        LoginController loginController = new LoginController();
        view.getScene().setRoot(loginController.getView());
    }
}
