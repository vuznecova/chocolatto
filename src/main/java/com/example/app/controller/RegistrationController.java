package com.example.app.controller;

import com.example.MainApp.java.MainApp;
import com.example.app.model.Role;
import com.example.app.model.User;
import com.example.app.utils.ViewUtils;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class RegistrationController {

    private StackPane rootPane;
    private VBox regBox;

    private TextField loginField;
    private PasswordField passwordField;
    private ComboBox<Role> roleCombo;
    private TextField adminCodeField;
    private Label adminCodeLabel;
    private Label messageLabel;

    public RegistrationController() {

        rootPane = ViewUtils.createStackPaneWithBackground("/images/background1.jpg");

        regBox = ViewUtils.createVBoxCenter(15, 15);
        regBox.setAlignment(Pos.CENTER);


        Label titleLabel = new Label("Register");
        titleLabel.setFont(Font.font(24));

        Label loginLbl = new Label("New login:");
        loginField = new TextField();
        loginField.setMaxWidth(200);

        Label passLbl = new Label("New password:");
        passwordField = new PasswordField();
        passwordField.setMaxWidth(200);

        Label roleLbl = new Label("Select role:");
        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(Role.CUSTOMER, Role.ADMIN);
        roleCombo.setValue(Role.CUSTOMER);
        roleCombo.setOnAction(e -> onRoleChanged());

        adminCodeLabel = new Label("Enter admin code:");
        adminCodeLabel.setVisible(false);
        adminCodeField = new TextField();
        adminCodeField.setMaxWidth(200);
        adminCodeField.setVisible(false);

        Button regBtn = new Button("Register");
        regBtn.setOnAction(e -> doRegister());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> goBack());

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        regBox.getChildren().addAll(
                titleLabel,
                loginLbl, loginField,
                passLbl, passwordField,
                roleLbl, roleCombo,
                adminCodeLabel, adminCodeField,
                regBtn, backBtn,
                messageLabel
        );

        rootPane.getChildren().add(regBox);
        StackPane.setAlignment(regBox, Pos.CENTER);
    }


    public StackPane getView() {
        return rootPane;
    }

    private void onRoleChanged() {
        if (roleCombo.getValue() == Role.ADMIN) {
            adminCodeLabel.setVisible(true);
            adminCodeField.setVisible(true);
        } else {
            adminCodeLabel.setVisible(false);
            adminCodeField.setVisible(false);
        }
    }

    private void doRegister() {
        String login = loginField.getText().trim();
        String pass = passwordField.getText().trim();
        Role chosenRole = roleCombo.getValue();

        if (login.isEmpty() || pass.isEmpty()) {
            messageLabel.setText("Login/password fields cannot be empty!");
            return;
        }

        if (chosenRole == Role.ADMIN) {
            String code = adminCodeField.getText().trim();
            if (!"132435".equals(code)) {
                messageLabel.setText("Incorrect admin code!");
                return;
            }
        }

        try {
            User newUser = MainApp.getUserService().register(login, pass, chosenRole);
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("User " + newUser.getLogin()
                    + " was registered as " + newUser.getRole() + "!");
        } catch (Exception ex) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void goBack() {
        LoginController loginController = new LoginController();
        rootPane.getScene().setRoot(loginController.getView());
    }
}
