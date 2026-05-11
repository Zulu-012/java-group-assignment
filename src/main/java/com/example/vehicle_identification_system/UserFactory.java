package com.example.vehicle_identification_system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFactory {

    // Factory method - returns appropriate User type based on role
    public static User createUser(String username, String password) {
        try (Connection conn = ConnectionDB.getConnection()) {
            // First check Users table for clients
            String clientSql = "SELECT username, email FROM Users WHERE username = ? AND password = ?";
            PreparedStatement clientStmt = conn.prepareStatement(clientSql);
            clientStmt.setString(1, username);
            clientStmt.setString(2, password);
            ResultSet clientRs = clientStmt.executeQuery();

            if (clientRs.next()) {
                String email = clientRs.getString("email");
                clientRs.close();
                clientStmt.close();
                return new Client(username, email);
            }
            clientRs.close();
            clientStmt.close();

            // Check for police user
            if (username.equals("police_user") && password.equals("police123")) {
                return new PoliceOfficer(username, "police@vehicleid.com", "POL-001", "Police Officer", "10111", "Central Station");
            }

            // Check for insurance user
            if (username.equals("insurance_user") && password.equals("insurance123")) {
                return new InsuranceAgent(username, "insurance@vehicleid.com", "INS-001", "Insurance Agent", "0860 123 456", "Vehicle Insurance");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to get user by username only (for session management)
    public static User getUserByUsername(String username) {
        try (Connection conn = ConnectionDB.getConnection()) {
            String sql = "SELECT username, email FROM Users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                rs.close();
                pstmt.close();
                return new Client(username, email);
            }
            rs.close();
            pstmt.close();

            if (username.equals("police_user")) {
                return new PoliceOfficer(username, "police@vehicleid.com", "POL-001");
            }

            if (username.equals("insurance_user")) {
                return new InsuranceAgent(username, "insurance@vehicleid.com", "INS-001");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}