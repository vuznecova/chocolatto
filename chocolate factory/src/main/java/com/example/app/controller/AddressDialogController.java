package com.example.app.controller;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddressDialogController {

    private Stage dialogStage;
    private TextField countryField;
    private TextField cityField;
    private TextField streetField;
    private TextField houseField;

    // Здесь мы храним результат, который потом может забрать ShopController
    private String addressResult;

    public AddressDialogController(Stage parentStage) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Введите адрес доставки");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label label1 = new Label("Страна:");
        countryField = new TextField();

        Label label2 = new Label("Город:");
        cityField = new TextField();

        Label label3 = new Label("Улица:");
        streetField = new TextField();

        Label label4 = new Label("Дом/Кв:");
        houseField = new TextField();

        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Отмена");

        okBtn.setOnAction(e -> onOk());
        cancelBtn.setOnAction(e -> onCancel());

        root.getChildren().addAll(
                label1, countryField,
                label2, cityField,
                label3, streetField,
                label4, houseField,
                okBtn, cancelBtn
        );

        Scene scene = new Scene(root, 300, 300);
        dialogStage.setScene(scene);
    }

    public void showDialog() {
        dialogStage.showAndWait();
    }

    private void onOk() {
        // Считываем все поля, собираем в одну строку (или делаем объект Address)
        String country = countryField.getText().trim();
        String city = cityField.getText().trim();
        String street = streetField.getText().trim();
        String house = houseField.getText().trim();

        // Минимальная проверка
        if (country.isEmpty() || city.isEmpty()
                || street.isEmpty() || house.isEmpty()) {
            // Можно показать Alert
            new Alert(Alert.AlertType.WARNING, "Пожалуйста, заполните все поля!")
                    .showAndWait();
            return;
        }

        // Запоминаем адрес
        this.addressResult = country + ", " + city + ", " + street + ", " + house;
        dialogStage.close();
    }

    private void onCancel() {
        this.addressResult = null; // ничего не возвращаем
        dialogStage.close();
    }

    public String getAddressResult() {
        return addressResult;
    }
}
