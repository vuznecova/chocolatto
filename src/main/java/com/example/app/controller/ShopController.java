package com.example.app.controller;

import com.example.MainApp.java.MainApp;
import com.example.app.model.Product;
import com.example.app.model.User;
import com.example.app.model.Order;
import com.example.app.utils.ViewUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ShopController {

    private StackPane rootPane;   // фон (бэкграунд)
    private BorderPane borderPane; // основная разметка: top/center/bottom/left/right

    private VBox itemsBox;         // контейнер для карточек товаров
    private Label cartLabel;       // метка «Корзина пуста»

    private User currentUser;
    private List<Product> cart;    // товары в корзине

    public ShopController(User user) {
        this.currentUser = user;
        this.cart = new ArrayList<>();

        // 1) Создаём корневой StackPane с фоном
        rootPane = ViewUtils.createStackPaneWithBackground("/images/background.jpg");

        // 2) Внутри StackPane размещаем BorderPane, чтобы удобно делить на части
        borderPane = new BorderPane();
        rootPane.getChildren().add(borderPane);
        StackPane.setAlignment(borderPane, Pos.CENTER);
        borderPane.setStyle("-fx-background-color: transparent;");
        borderPane.setMaxWidth(800);
        borderPane.setMaxHeight(600);
        StackPane.setMargin(borderPane, new Insets(50, 80, 50, 80));



        // ------------------ TOP: Приветствие ------------------
        Label welcomeLabel = new Label("Добро пожаловать, " + currentUser.getLogin() + "!");
        welcomeLabel.setPadding(new Insets(10));
        welcomeLabel.setStyle("-fx-font-size: 18px;");
        // Если хотите выровнять по центру
        HBox topBox = new HBox(welcomeLabel);
        topBox.setAlignment(Pos.CENTER);
        borderPane.setTop(topBox);

        // ------------------ CENTER: Прокручиваемый список товаров ------------------
        // VBox со всеми карточками
        itemsBox = new VBox(15);
        itemsBox.setAlignment(Pos.TOP_CENTER);
        itemsBox.setPadding(new Insets(20));
        itemsBox.setMaxWidth(900);

        // Создаём ScrollPane, помещаем туда itemsBox
        ScrollPane scrollPane = new ScrollPane(itemsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Помещаем ScrollPane в центр BorderPane
        borderPane.setCenter(scrollPane);

        // Загружаем список товаров
        List<Product> allProducts = MainApp.getProductService().getAllProducts();
        for (Product p : allProducts) {
            itemsBox.getChildren().add(createProductBox(p));
        }

        // ------------------ BOTTOM: Кнопки и информация о корзине ------------------
        // Создадим VBox (или HBox) с кнопками и меткой
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));

        cartLabel = new Label("Корзина пуста");

        Button orderBtn = new Button("Оформить заказ");
        Button myOrdersBtn = new Button("Мои заказы");
        Button cancelOrderBtn = new Button("Отменить заказ");
        Button logoutBtn = new Button("Выйти");

        // Привязываем логику
        orderBtn.setOnAction(e -> createOrder());
        myOrdersBtn.setOnAction(e -> showMyOrders());
        cancelOrderBtn.setOnAction(e -> cancelOrder());
        logoutBtn.setOnAction(e -> logout());

        // Добавляем в bottomBox
        bottomBox.getChildren().addAll(
                cartLabel,
                orderBtn,
                myOrdersBtn,
                cancelOrderBtn,
                logoutBtn
        );

        // Помещаем bottomBox в низ BorderPane
        borderPane.setBottom(bottomBox);
    }

    public StackPane getView() {
        return rootPane;
    }

    // ------------------------------------------
    // Создаём "карточку" товара
    // ------------------------------------------
    private HBox createProductBox(Product product) {
        HBox productBox = new HBox(10);
        productBox.setAlignment(Pos.CENTER_LEFT);
        productBox.setPadding(new Insets(10));
        productBox.setMaxWidth(800);
        productBox.setStyle("-fx-border-color: gray; -fx-padding: 5;");

        // Загружаем картинку (если есть)
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

    // ------------------------------------------
    // Загрузка картинки безопасно
    // ------------------------------------------
    private Image loadProductImage(String path) {
        try {
            if (path.startsWith("/")) {
                var url = getClass().getResource(path);
                if (url != null) {
                    return new Image(url.toExternalForm());
                } else {
                    System.out.println("Не найдена картинка: " + path);
                }
            } else {
                // Путь во внешней файловой системе
                return new Image("file:" + path);
            }
        } catch (Exception e) {
            System.out.println("Ошибка загрузки изображения: " + e.getMessage());
        }
        return null;
    }

    // ------------------------------------------
    // Оформление заказа
    // ------------------------------------------
    private void createOrder() {
        if (cart.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Корзина пуста!").showAndWait();
            return;
        }

        // Спросить адрес
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

        // 1) Считаем итоговую сумму
        double total = 0.0;
        for (Product p : cart) {
            total += p.getPrice();
        }

        // 2) Формируем описание позиций (если хотите показать админу)
        String itemsDesc = buildItemsDescription(cart)
                + "\nИтого: " + total + " $";

        // 3) Создаём заказ в OrderService (с новым параметром itemsDesc)
        //    Предположим, что createOrder(user, addr, itemsDesc) уже реализован.
        MainApp.getOrderService().createOrder(currentUser.getLogin(), address, itemsDesc);

        // 4) Очищаем корзину
        cart.clear();
        cartLabel.setText("Корзина пуста");

        // 5) Показываем сообщение пользователю
        new Alert(Alert.AlertType.INFORMATION,
                "Заказ оформлен!\nАдрес: " + address +
                        "\nИтоговая сумма: " + total + " $").showAndWait();
    }


    private String buildItemsDescription(List<Product> cart) {
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (Product p : cart) {
            sb.append(index++).append(") ")
                    .append(p.getName())
                    .append(" (").append(p.getPrice()).append(" $)\n");
        }
        return sb.toString().trim();
    }



    // ------------------------------------------
    // Просмотр заказов
    // ------------------------------------------
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

    // ------------------------------------------
    // Отмена (удаление) заказа
    // ------------------------------------------
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

        // Удаляем заказ
        MainApp.getOrderService().removeOrder(myOrders.get(index));

        new Alert(Alert.AlertType.INFORMATION,
                "Заказ отменён. Вам вернутся деньги в течение пары дней!")
                .showAndWait();
    }

    // ------------------------------------------
    // Выход
    // ------------------------------------------
    private void logout() {
        LoginController loginController = new LoginController();
        rootPane.getScene().setRoot(loginController.getView());
    }
}
