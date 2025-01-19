package com.example.app.utils;

import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class ViewUtils {

    public static StackPane createStackPaneWithBackground(String backgroundResourcePath) {
        StackPane rootPane = new StackPane();
        rootPane.setPrefSize(800, 600);

        BackgroundImage bgImage = new BackgroundImage(
                new Image(ViewUtils.class.getResource(backgroundResourcePath).toExternalForm()),
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

        return rootPane;
    }

    public static VBox createVBoxCenter(double spacing, double padding) {
        VBox box = new VBox(spacing);
        box.setPadding(new Insets(padding));
        box.setAlignment(Pos.CENTER);
        return box;
    }
}
