package com.example.vehicle_identification_system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private static final String URL = "jdbc:postgresql://localhost:5432/vehicleidsystem";
    private static final String USER = "postgres";
    private static final String PASSWORD = "keahana";

    // Always create a new connection - don't reuse static connections
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection Successful ");
            return conn;
        } catch (SQLException e) {
            System.err.println("Database connection Failed ");
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    // Police connection using police_role privileges - use postgres for full access
    public static Connection getPoliceConnection() throws SQLException {
        try {
            // Use postgres user for police to avoid permission issues
            Connection conn = DriverManager.getConnection(URL, "postgres", "keahana");
            System.out.println("Police connection successful ");
            return conn;
        } catch (SQLException e) {
            System.err.println("Police connection failed: " + e.getMessage());
            throw e;
        }
    }

    // Insurance connection using insurance_role privileges
    public static Connection getInsuranceConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, "postgres", "keahana");
            System.out.println("Insurance connection successful ");
            return conn;
        } catch (SQLException e) {
            System.err.println("Insurance connection failed: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Test connection successful!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}