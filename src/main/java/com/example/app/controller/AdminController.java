package com.example.app.controller;

import com.example.MainApp.java.MainApp;
import com.example.app.model.Order;
import com.example.app.model.OrderStatus;
import com.example.app.model.Product;
import com.example.app.utils.ViewUtils;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class AdminController {

    private StackPane rootPane;
    private VBox mainBox;

    private TextArea ordersArea;
    private List<Order> allOrders;
    private TextField orderIndexField;
    private ComboBox<OrderStatus> statusCombo;

    private TextArea productsArea;

    public AdminController() {
        rootPane = ViewUtils.createStackPaneWithBackground("/images/background1.jpg");
        mainBox = ViewUtils.createVBoxCenter(15, 20);
        rootPane.getChildren().add(mainBox);

        HBox ordersBox = new HBox(15);
        ordersBox.setMaxWidth(900);
        ordersBox.setStyle("-fx-border-color: gray; -fx-padding: 10;");
        ordersBox.setFillHeight(true);

        VBox ordersLeftBox = new VBox(10);
        Label header = new Label("Admin: all orders");

        ordersArea = new TextArea();
        ordersArea.setMaxWidth(400);
        ordersArea.setEditable(false);

        ordersLeftBox.getChildren().addAll(header, ordersArea);

        VBox ordersRightBox = new VBox(15);

        Button refreshOrdersBtn = new Button("Update all orders");
        refreshOrdersBtn.setOnAction(e -> loadOrders());

        Label indexLabel = new Label("Enter order number");
        orderIndexField = new TextField();
        orderIndexField.setMaxWidth(150);

        Label statusLbl = new Label("New status");
        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(
                OrderStatus.NEW,
                OrderStatus.IN_PROGRESS,
                OrderStatus.COMPLETED,
                OrderStatus.CANCELED
        );
        statusCombo.setValue(OrderStatus.NEW);

        Button setStatusBtn = new Button("Change status");
        setStatusBtn.setOnAction(e -> changeStatus());

        ordersRightBox.getChildren().addAll(
                refreshOrdersBtn,
                indexLabel, orderIndexField,
                statusLbl, statusCombo,
                setStatusBtn
        );

        ordersBox.getChildren().addAll(ordersLeftBox, ordersRightBox);

        mainBox.getChildren().add(ordersBox);

        HBox sepContainer = new HBox();
        sepContainer.setAlignment(Pos.CENTER);

        Separator shortSeparator = new Separator();
        shortSeparator.setPrefWidth(400);

        sepContainer.getChildren().add(shortSeparator);

        mainBox.getChildren().add(sepContainer);


        HBox productsBox = new HBox(15);
        productsBox.setMaxWidth(900);
        productsBox.setStyle("-fx-border-color: gray; -fx-padding: 10;");
        productsBox.setFillHeight(true);

        VBox productsLeftBox = new VBox(10);
        Label productsLabel = new Label("Products list");

        productsArea = new TextArea();
        productsArea.setMaxWidth(400);
        productsArea.setEditable(false);

        productsLeftBox.getChildren().addAll(productsLabel, productsArea);

        VBox productsRightBox = new VBox(10);

        Button refreshProductsBtn = new Button("Update products");
        refreshProductsBtn.setOnAction(e -> loadProducts());

        Button addProductBtn = new Button("Add product");
        addProductBtn.setOnAction(e -> openAddProductDialog());

        Button removeProductBtn = new Button("Delete product");
        removeProductBtn.setOnAction(e -> openRemoveProductDialog());

        productsRightBox.getChildren().addAll(
                refreshProductsBtn,
                addProductBtn,
                removeProductBtn
        );

        productsBox.getChildren().addAll(productsLeftBox, productsRightBox);
        mainBox.getChildren().add(productsBox);

        HBox sepContainer2 = new HBox();
        sepContainer2.setAlignment(Pos.CENTER);
        Separator shortSeparator2 = new Separator();
        shortSeparator2.setPrefWidth(400);
        sepContainer2.getChildren().add(shortSeparator2);

        mainBox.getChildren().add(sepContainer2);


        Button logoutBtn = new Button("Exit");
        logoutBtn.setOnAction(e -> logout());
        mainBox.getChildren().add(logoutBtn);

        loadOrders();
        loadProducts();
    }

    public StackPane getView() {
        return rootPane;
    }
    private void loadOrders() {
        allOrders = MainApp.getOrderService().getAllOrders();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allOrders.size(); i++) {
            Order o = allOrders.get(i);
            sb.append(i).append(") ")
                    .append("User: ").append(o.getUserLogin()).append(" | ")
                    .append("Date: ").append(o.getDateTime()).append(" | ")
                    .append("Status: ").append(o.getStatus()).append(" | ")
                    .append("Address: ").append(o.getAddress()).append("\n");
            if (o.getItemsDesc() != null && !o.getItemsDesc().isEmpty()) {
                sb.append("Products:\n").append(o.getItemsDesc()).append("\n");
            }
            sb.append("\n");
        }
        if (allOrders.isEmpty()) {
            sb.append("No orders right now");
        }
        ordersArea.setText(sb.toString());
    }

    private void changeStatus() {
        try {
            int index = Integer.parseInt(orderIndexField.getText());
            if (index < 0 || index >= allOrders.size()) {
                new Alert(Alert.AlertType.ERROR, "Incorrect ID").showAndWait();
                return;
            }
            Order o = allOrders.get(index);
            OrderStatus newStatus = statusCombo.getValue();
            MainApp.getOrderService().changeOrderStatus(o, newStatus);
            new Alert(Alert.AlertType.INFORMATION,
                    "Status was changed to " + newStatus).showAndWait();
            loadOrders();
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Enter correct order number").showAndWait();
        }
    }

    private void loadProducts() {
        List<Product> allProducts = MainApp.getProductService().getAllProducts();
        if (allProducts.isEmpty()) {
            productsArea.setText("No products");
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
                        "Price and product name should not be empty")
                        .showAndWait();
                return;
            }
            double priceVal;
            try {
                priceVal = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.WARNING,
                        "Price must be a number")
                        .showAndWait();
                return;
            }
            MainApp.getProductService().addProduct(name, priceVal, imagePath);

            new Alert(Alert.AlertType.INFORMATION,
                    "Product was added").showAndWait();
            loadProducts();
        }
    }

    private void openRemoveProductDialog() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setHeaderText("Enter product ID to remove");
        var result = inputDialog.showAndWait();
        if (result.isEmpty()) return;

        String indexStr = result.get();
        int index;
        try {
            index = Integer.parseInt(indexStr);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Incorrect ID format").showAndWait();
            return;
        }

        List<Product> allProducts = MainApp.getProductService().getAllProducts();
        if (index < 0 || index >= allProducts.size()) {
            new Alert(Alert.AlertType.ERROR, "No such ID").showAndWait();
            return;
        }

        MainApp.getProductService().removeProduct(index);
        new Alert(Alert.AlertType.INFORMATION, "Order was deleted").showAndWait();
        loadProducts();
    }

    private void logout() {
        LoginController loginController = new LoginController();
        rootPane.getScene().setRoot(loginController.getView());
    }
}
