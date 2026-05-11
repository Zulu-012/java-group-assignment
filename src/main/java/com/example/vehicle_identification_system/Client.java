package com.example.vehicle_identification_system;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends User {

    public Client(String username, String email) {
        super(username, email, "Client");
    }

    public Client(String username, String email, String fullName, String phoneNumber) {
        super(username, email, "Client", fullName, phoneNumber);
    }

    @Override
    public String getDashboardFXML() {
        return "/com/example/vehicle_identification_system/CustomerPage.fxml";
    }

    @Override
    public String getPermissions() {
        return "Can buy and sell vehicles, view vehicle history, contact sellers, manage own vehicles";
    }

    @Override
    public void loadDashboard(Stage stage, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(getDashboardFXML()));
            Parent root = loader.load();
            CustomerPage customerPage = loader.getController();
            customerPage.setLoggedInUser(username);
            stage.setScene(new Scene(root));
            stage.setTitle("Customer Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Client-specific methods
    public void sellVehicle() {
        System.out.println("Client " + username + " is selling a vehicle");
    }

    public void buyVehicle() {
        System.out.println("Client " + username + " is buying a vehicle");
    }
}