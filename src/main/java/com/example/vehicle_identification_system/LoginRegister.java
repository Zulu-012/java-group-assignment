package com.example.vehicle_identification_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRegister {

    @FXML
    private PasswordField LRPxtpassword;

    @FXML
    private Pane LRREgisterpane;

    @FXML
    private Button LRbtnLogin;

    @FXML
    private Button LRbtnRegister;

    @FXML
    private ImageView LRimageBakcground;

    @FXML
    private Label LRlblconfirmpassword;

    @FXML
    private Label LRlblemail;

    @FXML
    private Label LRlbllogin;

    @FXML
    private Label LRlblpassword;

    @FXML
    private Label LRlblregister;

    @FXML
    private Label LRlblusername;

    @FXML
    private Label LRlblusername2;

    @FXML
    private Pane LRloginCart;

    @FXML
    private PasswordField LRpxtConfirmPassword;

    @FXML
    private PasswordField LRpxtPassword;

    @FXML
    private TextField LRtxtUsername;

    @FXML
    private TextField LRtxtemail;

    @FXML
    private TextField LRtxtusername;

    private String loggedInUsername = "";

    @FXML
    void onclickRegister(ActionEvent event) {
        String username = LRtxtUsername.getText();
        String email = LRtxtemail.getText();
        String password = LRPxtpassword.getText();
        String confirmPassword = LRpxtConfirmPassword.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "All fields are required!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Passwords do not match!");
            return;
        }

        if (!email.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Invalid email format!");
            return;
        }

        try (Connection conn = ConnectionDB.getConnection()) {
            String sql = "INSERT INTO Users (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User registered successfully! Please login.");
                clearRegisterFields();
            }
            pstmt.close();
        } catch (SQLException e) {
            if (e.getMessage().contains("unique constraint")) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username or email already exists!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Database error: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    @FXML
    void onclickloginLR(ActionEvent event) {
        String username = LRtxtusername.getText();
        String password = LRpxtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Username and password are required!");
            return;
        }

        try (Connection conn = ConnectionDB.getConnection()) {
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                loggedInUsername = username;
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + username + "!");
                rs.close();
                pstmt.close();
                navigateToCustomerPage(event, username);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password!");
                rs.close();
                pstmt.close();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Database error: " + e.getMessage());
            e.printStackTrace();
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load customer page");
        }
    }

    private void clearRegisterFields() {
        LRtxtUsername.clear();
        LRtxtemail.clear();
        LRPxtpassword.clear();
        LRpxtConfirmPassword.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}