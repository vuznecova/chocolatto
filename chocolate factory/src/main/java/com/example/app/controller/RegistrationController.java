package com.example.app.controller;

import com.example.MainApp.java.MainApp;
import com.example.app.model.Role;
import com.example.app.model.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;


public class RegistrationController {
    private VBox view;

    private TextField loginField;
    private PasswordField passwordField;

    // Выбор роли
    private ComboBox<Role> roleCombo;

    // Специальное поле для кода «admin»
    private TextField adminCodeField;
    private Label adminCodeLabel;

    private Label messageLabel;

    public RegistrationController() {


        view = new VBox(10);
        view.setPadding(new Insets(10));

        Label loginLbl = new Label("Новый логин:");
        loginField = new TextField();

        Label passLbl = new Label("Новый пароль:");
        passwordField = new PasswordField();

        Label roleLbl = new Label("Выберите роль:");

        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(Role.CUSTOMER, Role.ADMIN);
        roleCombo.setValue(Role.CUSTOMER);

        // При смене значения в combo будем показывать/прятать поле «admin code»
        roleCombo.setOnAction(e -> onRoleChanged());

        adminCodeLabel = new Label("Введите код администратора:");
        adminCodeLabel.setVisible(false); // по умолчанию скрыто
        adminCodeField = new TextField();
        adminCodeField.setVisible(false);

        Button regBtn = new Button("Зарегистрировать");
        Button backBtn = new Button("Назад");

        regBtn.setOnAction(e -> doRegister());
        backBtn.setOnAction(e -> goBack());

        messageLabel = new Label();

        view.getChildren().addAll(
                loginLbl, loginField,
                passLbl, passwordField,
                roleLbl, roleCombo,
                adminCodeLabel, adminCodeField,
                regBtn, backBtn,
                messageLabel
        );
    }

    public VBox getView() {
        return view;
    }

    // Если выбрана роль ADMIN, показываем поле «admin code»,
    // иначе прячем
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
            messageLabel.setText("Логин/пароль не могут быть пустыми!");
            return;
        }

        // Если пользователь выбрал ADMIN, проверяем «admin code»
        if (chosenRole == Role.ADMIN) {
            String code = adminCodeField.getText().trim();
            if (!"132435".equals(code)) {
                messageLabel.setText("Неверный код администратора!");
                return;
            }
        }

        // Если всё в порядке, создаём пользователя
        try {
            User newUser = MainApp.getUserService().register(login, pass, chosenRole);
            messageLabel.setText("Пользователь " + newUser.getLogin()
                    + " зарегистрирован как " + newUser.getRole() + "!");
        } catch (Exception ex) {
            messageLabel.setText("Ошибка: " + ex.getMessage());
        }
    }

    private void goBack() {
        LoginController loginController = new LoginController();
        view.getScene().setRoot(loginController.getView());
    }
}

