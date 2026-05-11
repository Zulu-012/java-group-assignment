package com.example.vehicle_identification_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    @FXML
    private Button LbtnLogin;

    @FXML
    private Pane Lcart;

    @FXML
    private ImageView LimageBackground;

    @FXML
    private Label LlblLogin;

    @FXML
    private Label Llblusername;

    @FXML
    private Label Llbluserpassword;

    @FXML
    private PasswordField LpxtPassword;

    @FXML
    private TextField LtxtUsername;

    private String userType = "";
    private String loggedInUsername = "";

    public void setUserType(String type) {
        this.userType = type;
        if (type.equals("police")) {
            LlblLogin.setText("Police Login");
        } else if (type.equals("insurance")) {
            LlblLogin.setText("Insurance Login");
        } else {
            LlblLogin.setText("Login");
        }
    }

    @FXML
    void onclickLoginL(ActionEvent event) {
        String username = LtxtUsername.getText();
        String password = LpxtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Username and password are required!");
            return;
        }

        if (userType.equals("police")) {
            authenticatePolice(username, password, event);
        } else if (userType.equals("insurance")) {
            authenticateInsurance(username, password, event);
        } else {
            authenticateUser(username, password, event);
        }
    }

    private void authenticateUser(String username, String password, ActionEvent event) {
        try (Connection conn = ConnectionDB.getConnection()) {
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                loggedInUsername = username;
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + username + "!");
                navigateToCustomerPage(event, username);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password!");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void authenticatePolice(String username, String password, ActionEvent event) {
        if (username.equals("police_user") && password.equals("police123")) {
            loggedInUsername = username;
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome Police Officer!");
            navigateToPoliceDashboard(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid police credentials!");
        }
    }

    private void authenticateInsurance(String username, String password, ActionEvent event) {
        if (username.equals("insurance_user") && password.equals("insurance123")) {
            loggedInUsername = username;
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome Insurance Agent!");
            navigateToInsuranceDashboard(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid insurance credentials!");
        }
    }

    private void navigateToPoliceDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/PolicePage.fxml"));
            Parent root = loader.load();
            PolicePage policePage = loader.getController();
            policePage.setLoggedInOfficer(loggedInUsername);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Police Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Police Dashboard");
        }
    }

    private void navigateToInsuranceDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/InsurancePage.fxml"));
            Parent root = loader.load();
            InsurancePage insurancePage = loader.getController();
            insurancePage.setLoggedInAgent(loggedInUsername);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Insurance Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Insurance Dashboard");
        }
    }

    private void navigateToCustomerPage(ActionEvent event, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/CustomerPage.fxml"));
            Parent root = loader.load();
            CustomerPage customerPage = loader.getController();
            customerPage.setLoggedInUser(username);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Customer Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Customer Page");
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