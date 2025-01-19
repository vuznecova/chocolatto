module com.example.chocolatefactory {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.MainApp.java to javafx.fxml;
    exports com.example.MainApp.java;
}