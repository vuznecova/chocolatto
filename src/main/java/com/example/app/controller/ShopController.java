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

    private StackPane rootPane;
    private BorderPane borderPane;

    private VBox itemsBox;
    private Label cartLabel;

    private User currentUser;
    private List<Product> cart;

    public ShopController(User user) {
        this.currentUser = user;
        this.cart = new ArrayList<>();

        rootPane = ViewUtils.createStackPaneWithBackground("/images/background.jpg");

        borderPane = new BorderPane();
        rootPane.getChildren().add(borderPane);
        StackPane.setAlignment(borderPane, Pos.CENTER);
        borderPane.setStyle("-fx-background-color: transparent;");
        borderPane.setMaxWidth(800);
        borderPane.setMaxHeight(600);
        StackPane.setMargin(borderPane, new Insets(50, 80, 50, 80));



        Label welcomeLabel = new Label("Welcome, " + currentUser.getLogin() + "!");
        welcomeLabel.setPadding(new Insets(10));
        welcomeLabel.setStyle("-fx-font-size: 18px;");
        HBox topBox = new HBox(welcomeLabel);
        topBox.setAlignment(Pos.CENTER);
        borderPane.setTop(topBox);

        itemsBox = new VBox(15);
        itemsBox.setAlignment(Pos.TOP_CENTER);
        itemsBox.setPadding(new Insets(20));
        itemsBox.setMaxWidth(900);

        ScrollPane scrollPane = new ScrollPane(itemsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        borderPane.setCenter(scrollPane);

        List<Product> allProducts = MainApp.getProductService().getAllProducts();
        for (Product p : allProducts) {
            itemsBox.getChildren().add(createProductBox(p));
        }

        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));

        cartLabel = new Label("Cart is empty");

        Button orderBtn = new Button("Create order");
        Button myOrdersBtn = new Button("My orders");
        Button cancelOrderBtn = new Button("Cancel orders");
        Button logoutBtn = new Button("Exit");

        orderBtn.setOnAction(e -> createOrder());
        myOrdersBtn.setOnAction(e -> showMyOrders());
        cancelOrderBtn.setOnAction(e -> cancelOrder());
        logoutBtn.setOnAction(e -> logout());

        bottomBox.getChildren().addAll(
                cartLabel,
                orderBtn,
                myOrdersBtn,
                cancelOrderBtn,
                logoutBtn
        );

        borderPane.setBottom(bottomBox);
    }

    public StackPane getView() {
        return rootPane;
    }

    private HBox createProductBox(Product product) {
        HBox productBox = new HBox(10);
        productBox.setAlignment(Pos.CENTER_LEFT);
        productBox.setPadding(new Insets(10));
        productBox.setMaxWidth(800);
        productBox.setStyle("-fx-border-color: gray; -fx-padding: 5;");

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

        Button addBtn = new Button("Add to cart");
        addBtn.setOnAction(e -> {
            cart.add(product);
            cartLabel.setText("There are " + cart.size() + " products in the cart");
        });

        productBox.getChildren().addAll(imageView, productLabel, addBtn);
        return productBox;
    }

    private Image loadProductImage(String path) {
        try {
            if (path.startsWith("/")) {
                var url = getClass().getResource(path);
                if (url != null) {
                    return new Image(url.toExternalForm());
                } else {
                    System.out.println("Image not found: " + path);
                }
            } else {
                return new Image("file:" + path);
            }
        } catch (Exception e) {
            System.out.println("Image failed to load: " + e.getMessage());
        }
        return null;
    }

    private void createOrder() {
        if (cart.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Cart is empty!").showAndWait();
            return;
        }

        AddressDialogController dialog = new AddressDialogController(
                (Stage) rootPane.getScene().getWindow()
        );
        dialog.showDialog();

        String address = dialog.getAddressResult();
        if (address == null) {
            new Alert(Alert.AlertType.INFORMATION, "You cannot create an order without address.")
                    .showAndWait();
            return;
        }

        double total = 0.0;
        for (Product p : cart) {
            total += p.getPrice();
        }

        String itemsDesc = buildItemsDescription(cart)
                + "\nTotal: " + total + " $";

        MainApp.getOrderService().createOrder(currentUser.getLogin(), address, itemsDesc);

        cart.clear();
        cartLabel.setText("The cart is empty");

        new Alert(Alert.AlertType.INFORMATION,
                "Order was placed!\nAddress: " + address +
                        "\nYour total: " + total + " $").showAndWait();
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

    private void showMyOrders() {
        var myOrders = MainApp.getOrderService().getOrdersByUser(currentUser.getLogin());
        StringBuilder sb = new StringBuilder("My orders:\n");
        if (myOrders.isEmpty()) {
            sb.append("No orders");
        } else {
            for (var o : myOrders) {
                sb.append(o.getDateTime())
                        .append(" — ").append(o.getStatus())
                        .append(" | Address: ").append(o.getAddress())
                        .append("\n");
            }
        }
        new Alert(Alert.AlertType.INFORMATION, sb.toString()).showAndWait();
    }

    private void cancelOrder() {
        var myOrders = MainApp.getOrderService().getOrdersByUser(currentUser.getLogin());
        if (myOrders.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "You have no orders to cancel.").showAndWait();
            return;
        }

        StringBuilder sb = new StringBuilder("My orders:\n");
        for (int i = 0; i < myOrders.size(); i++) {
            sb.append(i).append(") ")
                    .append(myOrders.get(i).getDateTime()).append(" - ")
                    .append(myOrders.get(i).getStatus()).append(" | Address: ")
                    .append(myOrders.get(i).getAddress())
                    .append("\n");
        }
        new Alert(Alert.AlertType.INFORMATION, sb.toString()).showAndWait();

        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setHeaderText("Enter order number for cancellation:");
        var result = inputDialog.showAndWait();
        if (result.isEmpty()) return;

        String indexStr = result.get();
        int index;
        try {
            index = Integer.parseInt(indexStr);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Incorrect order number!").showAndWait();
            return;
        }

        if (index < 0 || index >= myOrders.size()) {
            new Alert(Alert.AlertType.ERROR, "No such order").showAndWait();
            return;
        }

        MainApp.getOrderService().removeOrder(myOrders.get(index));

        new Alert(Alert.AlertType.INFORMATION,
                "The order was cancelled! You will receive a refund in a couple of days!")
                .showAndWait();
    }

    private void logout() {
        LoginController loginController = new LoginController();
        rootPane.getScene().setRoot(loginController.getView());
    }
}
