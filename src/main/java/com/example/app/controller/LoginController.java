package com.example.app.controller;

import com.example.MainApp.java.MainApp;
import com.example.app.model.Role;
import com.example.app.model.User;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {

    private StackPane rootPane;
    private VBox loginBox;

    private TextField loginField;
    private PasswordField passwordField;
    private Label messageLabel;

    public LoginController() {
        rootPane = new StackPane();
        rootPane.setPrefSize(800, 600);

        BackgroundImage bgImage = new BackgroundImage(
                new Image(getClass().getResource("/images/background2.jpg").toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        BackgroundSize.AUTO,
                        BackgroundSize.AUTO,
                        true,
                        true,
                        true,
                        false
                )
        );
        rootPane.setBackground(new Background(bgImage));

        loginBox = new VBox(15);
        loginBox.setPadding(new Insets(20));
        loginBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Entering the system");
        titleLabel.setFont(Font.font(24));

        Label loginLbl = new Label("Login:");
        loginField = new TextField();
        loginField.setMaxWidth(200);

        Label passLbl = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setMaxWidth(200);

        Button registerBtn = new Button("Register");
        registerBtn.setOnAction(e -> {
            RegistrationController regController = new RegistrationController();
            rootPane.getScene().setRoot(regController.getView());
        });


        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        Button loginBtn = new Button("Login");
        Button exitBtn = new Button("Exit");



        loginBtn.setOnAction(e -> doLogin());
        exitBtn.setOnAction(e -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        });

        loginBox.getChildren().addAll(
                titleLabel,
                loginLbl, loginField,
                passLbl, passwordField,
                loginBtn,
                registerBtn,
                exitBtn,
                messageLabel
        );

        rootPane.getChildren().add(loginBox);
        StackPane.setAlignment(loginBox, Pos.CENTER);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), loginBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public StackPane getView() {
        return rootPane;
    }

    private void doLogin() {
        String login = loginField.getText().trim();
        String pass = passwordField.getText().trim();

        if (login.isEmpty() || pass.isEmpty()) {
            messageLabel.setText("Enter login and password!");
            return;
        }

        try {
            User user = MainApp.getUserService().login(login, pass);

            if (user.getRole() == Role.ADMIN) {
                AdminController adminController = new AdminController();
                rootPane.getScene().setRoot(adminController.getView());
            } else {
                ShopController shopController = new ShopController(user);
                rootPane.getScene().setRoot(shopController.getView());
            }

        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }
}
