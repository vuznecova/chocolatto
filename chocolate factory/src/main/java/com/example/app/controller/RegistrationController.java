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

    // Корневой контейнер с фоном
    private StackPane rootPane;
    // VBox, в котором лежат все поля и кнопки (по центру)
    private VBox regBox;

    private TextField loginField;
    private PasswordField passwordField;
    private ComboBox<Role> roleCombo;
    private TextField adminCodeField;
    private Label adminCodeLabel;
    private Label messageLabel;

    public RegistrationController() {

        // 1. Создаём корневой StackPane с задним фоном
        rootPane = ViewUtils.createStackPaneWithBackground("/images/background.jpg");

        // 2. Создаём VBox, центрируем
        regBox = ViewUtils.createVBoxCenter(15, 15);
        regBox.setAlignment(Pos.CENTER);


        // Заголовок (по желанию)
        Label titleLabel = new Label("Регистрация");
        titleLabel.setFont(Font.font(24));

        // Поля для логина/пароля
        Label loginLbl = new Label("Новый логин:");
        loginField = new TextField();
        loginField.setMaxWidth(200);

        Label passLbl = new Label("Новый пароль:");
        passwordField = new PasswordField();
        passwordField.setMaxWidth(200);

        // Роль и выбор ADMIN/USER
        Label roleLbl = new Label("Выберите роль:");
        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(Role.CUSTOMER, Role.ADMIN);
        roleCombo.setValue(Role.CUSTOMER);
        roleCombo.setOnAction(e -> onRoleChanged());

        // Поле «admin code»
        adminCodeLabel = new Label("Введите код администратора:");
        adminCodeLabel.setVisible(false);
        adminCodeField = new TextField();
        adminCodeField.setMaxWidth(200);
        adminCodeField.setVisible(false);

        // Кнопки
        Button regBtn = new Button("Зарегистрировать");
        regBtn.setOnAction(e -> doRegister());

        Button backBtn = new Button("Назад");
        backBtn.setOnAction(e -> goBack());

        // Сообщение об ошибке/успехе
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;"); // красный текст при ошибках

        // 3. Добавляем всё в regBox
        regBox.getChildren().addAll(
                titleLabel,
                loginLbl, loginField,
                passLbl, passwordField,
                roleLbl, roleCombo,
                adminCodeLabel, adminCodeField,
                regBtn, backBtn,
                messageLabel
        );

        // 4. Добавляем regBox в rootPane и выравниваем по центру
        rootPane.getChildren().add(regBox);
        StackPane.setAlignment(regBox, Pos.CENTER);
    }

    /**
     * Возвращаем корневой StackPane (с фоном),
     * чтобы при переключении сцен фон был виден.
     */
    public StackPane getView() {
        return rootPane;
    }

    // Если выбрана роль ADMIN, показываем поле «admin code»
    // иначе скрываем
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

        // Проверка «admin code»
        if (chosenRole == Role.ADMIN) {
            String code = adminCodeField.getText().trim();
            if (!"132435".equals(code)) {
                messageLabel.setText("Неверный код администратора!");
                return;
            }
        }

        try {
            User newUser = MainApp.getUserService().register(login, pass, chosenRole);
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Пользователь " + newUser.getLogin()
                    + " зарегистрирован как " + newUser.getRole() + "!");
        } catch (Exception ex) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Ошибка: " + ex.getMessage());
        }
    }

    private void goBack() {
        // Возврат на экран логина
        LoginController loginController = new LoginController();
        rootPane.getScene().setRoot(loginController.getView());
    }
}
