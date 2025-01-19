package com.example.MainApp.java;

import com.example.app.controller.LoginController;
import com.example.app.service.OrderService;
import com.example.app.service.ProductService;
import com.example.app.service.UserService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {
    private static UserService userService;
    private static OrderService orderService;

    public static UserService getUserService() {
        return userService;
    }


    public static OrderService getOrderService() {
        return orderService;
    }

    private static ProductService productService;

    public static ProductService getProductService() {
        return productService;
    }

    @Override
    public void start(Stage primaryStage) {
        userService = new UserService();
        orderService = new OrderService();
        productService = new ProductService();

        LoginController loginController = new LoginController();
        Scene scene = new Scene(loginController.getView(), 1200, 800);

        primaryStage.setTitle("Chocolate Factory App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}