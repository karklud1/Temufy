module com.example.temufy {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.temufy to javafx.fxml;
    exports com.example.temufy;
}