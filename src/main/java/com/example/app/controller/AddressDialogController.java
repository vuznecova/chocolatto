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

    private String addressResult;

    public AddressDialogController(Stage parentStage) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Enter delivery address");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label label1 = new Label("Country:");
        countryField = new TextField();

        Label label2 = new Label("City:");
        cityField = new TextField();

        Label label3 = new Label("Street:");
        streetField = new TextField();

        Label label4 = new Label("Apt/House number:");
        houseField = new TextField();

        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

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
        String country = countryField.getText().trim();
        String city = cityField.getText().trim();
        String street = streetField.getText().trim();
        String house = houseField.getText().trim();

        if (country.isEmpty() || city.isEmpty()
                || street.isEmpty() || house.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields!")
                    .showAndWait();
            return;
        }

        this.addressResult = country + ", " + city + ", " + street + ", " + house;
        dialogStage.close();
    }

    private void onCancel() {
        this.addressResult = null;
        dialogStage.close();
    }

    public String getAddressResult() {
        return addressResult;
    }
}
