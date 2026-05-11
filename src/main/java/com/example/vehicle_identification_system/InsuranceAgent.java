package com.example.vehicle_identification_system;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class InsuranceAgent extends User {
    private String agentId;
    private String department;

    public InsuranceAgent(String username, String email, String agentId) {
        super(username, email, "Insurance Agent");
        this.agentId = agentId;
        this.department = "Vehicle Insurance";
    }

    public InsuranceAgent(String username, String email, String agentId, String fullName, String phoneNumber, String department) {
        super(username, email, "Insurance Agent", fullName, phoneNumber);
        this.agentId = agentId;
        this.department = department;
    }

    @Override
    public String getDashboardFXML() {
        return "/com/example/vehicle_identification_system/InsurancePage.fxml";
    }

    @Override
    public String getPermissions() {
        return "Can manage policies, process claims, view insurance records, approve/reject claims";
    }

    @Override
    public void loadDashboard(Stage stage, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(getDashboardFXML()));
            Parent root = loader.load();
            InsurancePage insurancePage = loader.getController();
            insurancePage.setLoggedInAgent(username);
            stage.setScene(new Scene(root));
            stage.setTitle("Insurance Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Insurance-specific methods
    public void createPolicy(String vehicleNumber, String policyNumber, double premium) {
        System.out.println("Agent " + agentId + " created policy " + policyNumber + " for vehicle " + vehicleNumber);
    }

    public void processClaim(String claimId, String status) {
        System.out.println("Agent " + agentId + " " + status + " claim " + claimId);
    }

    public String getAgentId() { return agentId; }
    public String getDepartment() { return department; }
}