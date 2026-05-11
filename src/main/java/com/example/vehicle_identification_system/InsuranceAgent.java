package com.example.vehicle_identification_system;

public class InsuranceAgent extends User {
    private String agentId;

    public InsuranceAgent(String username, String email, String agentId) {
        super(username, email, "Insurance Agent");
        this.agentId = agentId;
    }

    @Override
    public String getDashboard() {
        return "InsurancePage.fxml";
    }

    @Override
    public String getPermissions() {
        return "Can manage policies, process claims, view insurance records";
    }

    public String getAgentId() { return agentId; }
}