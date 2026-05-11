package com.example.vehicle_identification_system;

public class PoliceOfficer extends User {
    private String badgeNumber;

    public PoliceOfficer(String username, String email, String badgeNumber) {
        super(username, email, "Police Officer");
        this.badgeNumber = badgeNumber;
    }

    @Override
    public String getDashboard() {
        return "PolicePage.fxml";
    }

    @Override
    public String getPermissions() {
        return "Can create reports, record violations, view vehicle records";
    }

    public String getBadgeNumber() { return badgeNumber; }
}