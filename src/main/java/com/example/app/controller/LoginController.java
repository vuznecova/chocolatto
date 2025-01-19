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

    // Главный контейнер
    private StackPane rootPane;
    // Контейнер для полей логина/пароля и кнопок
    private VBox loginBox;

    // Поля ввода и метки
    private TextField loginField;
    private PasswordField passwordField;
    private Label messageLabel;

    public LoginController() {
        // Создаём корневой StackPane, чтобы фон (картинка) был сзади,
        // а VBox c полями логина — по центру.
        rootPane = new StackPane();
        rootPane.setPrefSize(800, 600); // Размер окна по умолчанию

        // Устанавливаем фоновую картинку (предположим, лежит в resources/images/background.jpg)
        // Убедитесь, что файл действительно есть в папке resources/images.
        BackgroundImage bgImage = new BackgroundImage(
                new Image(getClass().getResource("/images/background2.jpg").toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        BackgroundSize.AUTO,
                        BackgroundSize.AUTO,
                        true,    // ширина подгоняется
                        true,    // высота подгоняется
                        true,    // сохранять соотношение сторон
                        false
                )
        );
        rootPane.setBackground(new Background(bgImage));

        // Создаём VBox для полей логина и кнопок
        loginBox = new VBox(15);
        loginBox.setPadding(new Insets(20));
        loginBox.setAlignment(Pos.CENTER);

        // Можно поднастроить размер или шрифт, чтобы при развороте окна смотрелось пропорционально
        Label titleLabel = new Label("Вход в систему");
        titleLabel.setFont(Font.font(24));

        Label loginLbl = new Label("Логин:");
        loginField = new TextField();
        loginField.setMaxWidth(200);

        Label passLbl = new Label("Пароль:");
        passwordField = new PasswordField();
        passwordField.setMaxWidth(200);

        Button registerBtn = new Button("Регистрация");
        registerBtn.setOnAction(e -> {
            RegistrationController regController = new RegistrationController();
            rootPane.getScene().setRoot(regController.getView());
        });


        messageLabel = new Label(); // Сюда выводим сообщения об ошибках
        messageLabel.setStyle("-fx-text-fill: red;");

        Button loginBtn = new Button("Войти");
        Button exitBtn = new Button("Выход");



        // При нажатии "Войти" пробуем авторизоваться
        loginBtn.setOnAction(e -> doLogin());
        // При нажатии "Выход" — закрываем приложение
        exitBtn.setOnAction(e -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        });

        // Добавляем все элементы во VBox
        loginBox.getChildren().addAll(
                titleLabel,
                loginLbl, loginField,
                passLbl, passwordField,
                loginBtn,
                registerBtn,   // <--- добавляем кнопку регистрации
                exitBtn,
                messageLabel
        );

        // Добавим VBox в центр StackPane
        rootPane.getChildren().add(loginBox);
        StackPane.setAlignment(loginBox, Pos.CENTER);

        // Для наглядности — анимация при появлении loginBox
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), loginBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    /**
     * Метод возвращает корневой узел (StackPane),
     * чтобы MainApp мог установить его как сцену.
     */
    public StackPane getView() {
        return rootPane;
    }

    private void doLogin() {
        String login = loginField.getText().trim();
        String pass = passwordField.getText().trim();

        // Проверяем, что поля не пусты
        if (login.isEmpty() || pass.isEmpty()) {
            messageLabel.setText("Введите логин и пароль!");
            return;
        }

        try {
            // Пытаемся авторизовать через UserService
            User user = MainApp.getUserService().login(login, pass);

            // Если пароль верный — проверяем роль
            if (user.getRole() == Role.ADMIN) {
                // Переход в админ-панель
                AdminController adminController = new AdminController();
                rootPane.getScene().setRoot(adminController.getView());
            } else {
                // Обычный пользователь
                // Переход в "магазин" или куда у вас положено
                ShopController shopController = new ShopController(user);
                rootPane.getScene().setRoot(shopController.getView());
            }

        } catch (Exception ex) {
            // Любая ошибка — выводим сообщение
            messageLabel.setText("Ошибка: " + ex.getMessage());
        }
    }
}
