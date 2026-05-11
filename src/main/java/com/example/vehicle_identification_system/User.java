package com.example.vehicle_identification_system;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class User {
    protected String username;
    protected String email;
    protected String role;
    protected String fullName;
    protected String phoneNumber;

    public User(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.fullName = username;
        this.phoneNumber = "";
    }

    public User(String username, String email, String role, String fullName, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // Abstract methods for polymorphism
    public abstract String getDashboardFXML();
    public abstract String getPermissions();
    public abstract void loadDashboard(Stage stage, String username);

    // Common methods for all users
    public void showUserInfo() {
        System.out.println("User: " + username + " | Role: " + role + " | Email: " + email);
    }

    public boolean hasPermission(String permission) {
        return getPermissions().contains(permission);
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // Common navigation method
    protected void navigateToPage(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}