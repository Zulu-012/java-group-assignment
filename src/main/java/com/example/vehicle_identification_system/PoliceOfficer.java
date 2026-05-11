package com.example.vehicle_identification_system;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PoliceOfficer extends User {
    private String badgeNumber;
    private String station;

    public PoliceOfficer(String username, String email, String badgeNumber) {
        super(username, email, "Police Officer");
        this.badgeNumber = badgeNumber;
        this.station = "Central Station";
    }

    public PoliceOfficer(String username, String email, String badgeNumber, String fullName, String phoneNumber, String station) {
        super(username, email, "Police Officer", fullName, phoneNumber);
        this.badgeNumber = badgeNumber;
        this.station = station;
    }

    @Override
    public String getDashboardFXML() {
        return "/com/example/vehicle_identification_system/PolicePage.fxml";
    }

    @Override
    public String getPermissions() {
        return "Can create reports, record violations, view vehicle records, access criminal database";
    }

    @Override
    public void loadDashboard(Stage stage, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(getDashboardFXML()));
            Parent root = loader.load();
            PolicePage policePage = loader.getController();
            policePage.setLoggedInOfficer(username);
            stage.setScene(new Scene(root));
            stage.setTitle("Police Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Police-specific methods
    public void fileReport(String reportType, String details) {
        System.out.println("Officer " + badgeNumber + " filing " + reportType + " report: " + details);
    }

    public void recordViolation(String registrationNumber, String violationType, double fine) {
        System.out.println("Officer " + badgeNumber + " recorded violation for " + registrationNumber);
    }

    public String getBadgeNumber() { return badgeNumber; }
    public String getStation() { return station; }
}