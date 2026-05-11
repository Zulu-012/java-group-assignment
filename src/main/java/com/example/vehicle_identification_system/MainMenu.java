package com.example.vehicle_identification_system;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenu {

    @FXML
    private Button MMbtnViewmore;

    @FXML
    private Button MMbtnViewmorePolice;

    @FXML
    private Button MMbtnViewmoreInsurance;

    @FXML
    private Pane MMcartClient;

    @FXML
    private ImageView MMimagebackground;

    @FXML
    private Pane MMinsuranceCart;

    @FXML
    private Label MMlblClientandSellers;

    @FXML
    private Label MMlblInsurance;

    @FXML
    private Label MMlblSystem;

    @FXML
    private Label MMlblbrowsecars;

    @FXML
    private Label MMlblidentification;

    @FXML
    private Label MMlblpoliceAndSecurity;

    @FXML
    private Label MMlblsecure;

    @FXML
    private Label MMlblslogen;

    @FXML
    private Label MMlblvehicle;

    @FXML
    private Label MMlblwetrack;

    @FXML
    private Pane MMpoliceCart;

    @FXML
    private Pane root;

    @FXML
    void handleExit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void handleAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Vehicle Identification System");
        alert.setHeaderText("Vehicle Identification System v1.0");
        alert.setContentText("A comprehensive vehicle management system.\n\n" +
                "Features:\n" +
                "• Buy and Sell Vehicles\n" +
                "• Insurance Management\n" +
                "• Police Reports and Violations\n" +
                "• Customer Support\n\n" +
                "© 2025 Vehicle Identification System");
        alert.showAndWait();
    }

    @FXML
    void viewClientsAndSellers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/LoginAndRegister.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Client Login & Register");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Login page");
        }
    }

    @FXML
    void viewMoreInsurance(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/login.fxml"));
            Parent root = loader.load();
            Login loginController = loader.getController();
            loginController.setUserType("insurance");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Insurance Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Insurance Login page");
        }
    }

    @FXML
    void viewmorePolice(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/login.fxml"));
            Parent root = loader.load();
            Login loginController = loader.getController();
            loginController.setUserType("police");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Police Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Police Login page");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}