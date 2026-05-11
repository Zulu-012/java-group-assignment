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
    private User currentUser = null;

    public void setUserType(String type) {
        this.userType = type;
        if (type.equals("police")) {
            LlblLogin.setText("Login");
        } else if (type.equals("insurance")) {
            LlblLogin.setText("Login");
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

        currentUser = UserFactory.createUser(username, password);

        if (currentUser != null) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful",
                    "Welcome " + currentUser.getFullName() + "!\nRole: " + currentUser.getRole() +
                            "\nPermissions: " + currentUser.getPermissions());
            currentUser.loadDashboard((Stage) ((Node) event.getSource()).getScene().getWindow(), username);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password!");
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