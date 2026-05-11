package com.example.vehicle_identification_system;

public class Client extends User {

    public Client(String username, String email) {
        super(username, email, "Client");
    }

    @Override
    public String getDashboard() {
        return "CustomerPage.fxml";
    }

    @Override
    public String getPermissions() {
        return "Can buy and sell vehicles, view vehicle history, contact sellers";
    }
}