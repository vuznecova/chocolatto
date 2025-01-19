package com.example.app.controller;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddProductDialogController {
    private Stage dialogStage;

    private TextField nameField;
    private TextField priceField;
    private TextField imagePathField;

    private boolean okClicked = false;

    public AddProductDialogController(Stage parentStage) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Add new product");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Label nameLbl = new Label("Product name:");
        nameField = new TextField();

        Label priceLbl = new Label("Price:");
        priceField = new TextField();

        Label imageLbl = new Label("Path to image:");
        imagePathField = new TextField();

        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Cancel");

        okBtn.setOnAction(e -> {
            okClicked = true;
            dialogStage.close();
        });
        cancelBtn.setOnAction(e -> {
            dialogStage.close();
        });

        root.getChildren().addAll(nameLbl, nameField,
                priceLbl, priceField,
                imageLbl, imagePathField,
                okBtn, cancelBtn);

        dialogStage.setScene(new Scene(root, 400, 300));
    }

    public void showDialog() {
        dialogStage.showAndWait();
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public String getProductName() {
        return nameField.getText();
    }

    public String getPrice() {
        return priceField.getText();
    }

    public String getImagePath() {
        return imagePathField.getText();
    }
}
