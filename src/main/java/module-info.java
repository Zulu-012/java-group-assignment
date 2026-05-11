module com.example.vehicle_identification_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.vehicle_identification_system to javafx.fxml;
    exports com.example.vehicle_identification_system;
}