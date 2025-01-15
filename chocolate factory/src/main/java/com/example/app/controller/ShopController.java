package com.example.app.controller;

import com.example.MainApp.java.MainApp;
import com.example.app.model.Product;
import com.example.app.model.User;
import com.example.app.model.Order;
import com.example.app.utils.ViewUtils;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ShopController {
    private StackPane rootPane;   // фон
    private VBox mainBox;         // контейнер элементов

    private Label cartLabel;
    private User currentUser;

    private List<Product> cart;   // товары, которые пользователь выбрал

    public ShopController(User user) {
        this.currentUser = user;
        this.cart = new ArrayList<>();

        // Создаём фон и VBox
        rootPane = ViewUtils.createStackPaneWithBackground("/images/background.jpg");
        mainBox = ViewUtils.createVBoxCenter(15, 20);
        rootPane.getChildren().add(mainBox);

        Label welcomeLabel = new Label("Добро пожаловать, " + currentUser.getLogin() + "!");
        mainBox.getChildren().add(welcomeLabel);

        // Список товаров
        List<Product> allProducts = MainApp.getProductService().getAllProducts();
        for (Product p : allProducts) {
            mainBox.getChildren().add(createProductBox(p));
            mainBox.setMaxWidth(900);
        }

        cartLabel = new Label("Корзина пуста");
        Button orderBtn = new Button("Оформить заказ");
        Button myOrdersBtn = new Button("Мои заказы");
        Button cancelOrderBtn = new Button("Отменить заказ");
        Button logoutBtn = new Button("Выйти");

        orderBtn.setOnAction(e -> createOrder());
        myOrdersBtn.setOnAction(e -> showMyOrders());
        cancelOrderBtn.setOnAction(e -> cancelOrder());
        logoutBtn.setOnAction(e -> logout());

        mainBox.getChildren().addAll(
                cartLabel,
                orderBtn,
                myOrdersBtn,
                cancelOrderBtn,
                logoutBtn
        );
    }

    public StackPane getView() {
        return rootPane;
    }

    // ------------------------------------------------
    // Отображение товара (с безопасной проверкой картинки)
    // ------------------------------------------------
    private HBox createProductBox(Product product) {
        HBox productBox = new HBox(10);
        productBox.setPadding(new Insets(10));
        productBox.setMaxWidth(900);
        productBox.setStyle("-fx-border-color: gray; -fx-padding: 5;");

        // Попытаемся загрузить картинку
        ImageView imageView = new ImageView();
        String path = product.getImagePath();
        if (path != null && !path.isEmpty()) {
            Image img = loadProductImage(path);
            if (img != null) {
                imageView.setImage(img);
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);
            }
        }

        Label productLabel = new Label(product.getName() + " - " + product.getPrice() + " $");

        Button addBtn = new Button("В корзину");
        addBtn.setOnAction(e -> {
            cart.add(product);
            cartLabel.setText("В корзине: " + cart.size() + " товар(ов)");
        });

        productBox.getChildren().addAll(imageView, productLabel, addBtn);
        return productBox;
    }

    /**
     * Безопасно загружаем картинку.
     * Если путь начинается со слэша — считаем, что это ресурс в /resources/.
     * Иначе пробуем "file:" + path.
     */
    private Image loadProductImage(String path) {
        try {
            if (path.startsWith("/")) {
                var url = getClass().getResource(path);
                if (url != null) {
                    return new Image(url.toExternalForm());
                } else {
                    System.out.println("Не найдена картинка в ресурсах: " + path);
                    return null;
                }
            } else {
                // Допустим, это путь в файловой системе
                return new Image("file:" + path);
            }
        } catch (Exception e) {
            System.out.println("Ошибка загрузки изображения: " + e.getMessage());
            return null;
        }
    }

    // ------------------------------------------------
    // Оформление заказа
    // ------------------------------------------------
    private void createOrder() {
        if (cart.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Корзина пуста!").showAndWait();
            return;
        }
        // Спрашиваем адрес
        AddressDialogController dialog = new AddressDialogController(
                (Stage) rootPane.getScene().getWindow()
        );
        dialog.showDialog();

        String address = dialog.getAddressResult();
        if (address == null) {
            new Alert(Alert.AlertType.INFORMATION, "Заказ не оформлен без адреса.")
                    .showAndWait();
            return;
        }

        MainApp.getOrderService().createOrder(currentUser.getLogin(), address);
        cart.clear();
        cartLabel.setText("Корзина пуста");
        new Alert(Alert.AlertType.INFORMATION,
                "Заказ оформлен!\nАдрес: " + address).showAndWait();
    }

    // ------------------------------------------------
    // Просмотр заказов
    // ------------------------------------------------
    private void showMyOrders() {
        var myOrders = MainApp.getOrderService().getOrdersByUser(currentUser.getLogin());
        StringBuilder sb = new StringBuilder("Мои заказы:\n");
        if (myOrders.isEmpty()) {
            sb.append("Нет заказов");
        } else {
            for (var o : myOrders) {
                sb.append(o.getDateTime())
                        .append(" — ").append(o.getStatus())
                        .append(" | Адрес: ").append(o.getAddress())
                        .append("\n");
            }
        }
        new Alert(Alert.AlertType.INFORMATION, sb.toString()).showAndWait();
    }

    // ------------------------------------------------
    // Отмена (удаление) заказа
    // ------------------------------------------------
    private void cancelOrder() {
        var myOrders = MainApp.getOrderService().getOrdersByUser(currentUser.getLogin());
        if (myOrders.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "У вас нет заказов для отмены.").showAndWait();
            return;
        }

        // Показываем список
        StringBuilder sb = new StringBuilder("Мои заказы:\n");
        for (int i = 0; i < myOrders.size(); i++) {
            sb.append(i).append(") ")
                    .append(myOrders.get(i).getDateTime()).append(" - ")
                    .append(myOrders.get(i).getStatus()).append(" | Адрес: ")
                    .append(myOrders.get(i).getAddress())
                    .append("\n");
        }
        new Alert(Alert.AlertType.INFORMATION, sb.toString()).showAndWait();

        // Спрашиваем индекс
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setHeaderText("Введите номер заказа для отмены:");
        var result = inputDialog.showAndWait();
        if (result.isEmpty()) return;

        String indexStr = result.get();
        int index;
        try {
            index = Integer.parseInt(indexStr);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Неверный номер заказа!").showAndWait();
            return;
        }

        if (index < 0 || index >= myOrders.size()) {
            new Alert(Alert.AlertType.ERROR, "Такого заказа нет!").showAndWait();
            return;
        }

        // Удаляем заказ из OrderService
        MainApp.getOrderService().removeOrder(myOrders.get(index));

        new Alert(Alert.AlertType.INFORMATION,
                "Заказ отменён. Вам вернутся деньги в течение пары дней!")
                .showAndWait();
    }

    // ------------------------------------------------
    // Выход
    // ------------------------------------------------
    private void logout() {
        LoginController loginController = new LoginController();
        rootPane.getScene().setRoot(loginController.getView());
    }
}
