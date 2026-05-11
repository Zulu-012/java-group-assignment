package com.example.vehicle_identification_system;

public abstract class User {
    protected String username;
    protected String email;
    protected String role;

    public User(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public abstract String getDashboard();
    public abstract String getPermissions();

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}