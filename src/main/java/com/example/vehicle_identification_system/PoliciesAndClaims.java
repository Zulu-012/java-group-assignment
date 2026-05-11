package com.example.vehicle_identification_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PoliciesAndClaims {

    @FXML
    private Pane PCClaimCart;

    @FXML
    private DatePicker PCTxtDate;

    @FXML
    private Button PCbtnAddpolicy;

    @FXML
    private Button PCbtnMakeclaim;

    @FXML
    private Button PCbtnback;

    @FXML
    private ImageView PCimagebackground;

    @FXML
    private Label PClblAmount;

    @FXML
    private Label PClblClaimId;

    @FXML
    private Label PClblClaimStatus;

    @FXML
    private Label PClblDate;

    @FXML
    private Label PClblEndDate;

    @FXML
    private Label PClblHeader;

    @FXML
    private Label PClblInsuranceCompany;

    @FXML
    private Label PClblPolicy;

    @FXML
    private Label PClblStartDate;

    @FXML
    private Label PClblVehicleNumner2;

    @FXML
    private Label PClblcoverageDeatials;

    @FXML
    private Label PClblpolicyNumber;

    @FXML
    private Label PClblpolicyStatus;

    @FXML
    private Label PClblvehicleNumber;

    @FXML
    private Pane PCpolicyCart;

    @FXML
    private TextField PCtxtClaimID;

    @FXML
    private TextArea PCtxtCoverageDetails;

    @FXML
    private DatePicker PCtxtEndDateP;

    @FXML
    private DatePicker PCtxtStartDateP;

    @FXML
    private TextField PCtxtVehicleNumber;

    @FXML
    private TextField PCtxtVehiclenumber;

    @FXML
    private TextField PCtxtamount;

    @FXML
    private TextField PCtxtinsurancecompany;

    @FXML
    private TextField PCtxtpolicyNumber;

    @FXML
    private TextField PCtxtpolicynumber;

    private String loggedInAgent = "";

    public void setLoggedInAgent(String username) {
        this.loggedInAgent = username;
    }

    @FXML
    void onclickAddpolicy(ActionEvent event) {
        String policyNumber = PCtxtpolicynumber.getText();
        String vehicleNumber = PCtxtVehicleNumber.getText();
        String insuranceCompany = PCtxtinsurancecompany.getText();
        LocalDate startDate = PCtxtStartDateP.getValue();
        LocalDate endDate = PCtxtEndDateP.getValue();
        String coverageDetails = PCtxtCoverageDetails.getText();

        if (policyNumber.isEmpty() || vehicleNumber.isEmpty() || insuranceCompany.isEmpty() || startDate == null || endDate == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all required fields!");
            return;
        }

        try (Connection conn = ConnectionDB.getConnection()) {
            // Get vehicle_id from registration number
            String vehicleSql = "SELECT vehicle_id FROM Vehicle WHERE registration_number = ?";
            PreparedStatement vehicleStmt = conn.prepareStatement(vehicleSql);
            vehicleStmt.setString(1, vehicleNumber);
            ResultSet rs = vehicleStmt.executeQuery();

            int vehicleId = 1;
            if (rs.next()) {
                vehicleId = rs.getInt("vehicle_id");
            } else {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Vehicle not found with registration number: " + vehicleNumber);
                rs.close();
                vehicleStmt.close();
                return;
            }
            rs.close();
            vehicleStmt.close();

            String sql = "INSERT INTO InsurancePolicy (vehicle_id, insurance_company, policy_number, start_date, end_date, coverage_details) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, vehicleId);
            pstmt.setString(2, insuranceCompany);
            pstmt.setString(3, policyNumber);
            pstmt.setDate(4, java.sql.Date.valueOf(startDate));
            pstmt.setDate(5, java.sql.Date.valueOf(endDate));
            pstmt.setString(6, coverageDetails);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Policy added successfully!");
                clearPolicyFields();
                PClblpolicyStatus.setText("Status: Added ✓");
            }
            pstmt.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add policy: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onclickBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/InsurancePage.fxml"));
            Parent root = loader.load();
            InsurancePage insurancePage = loader.getController();
            insurancePage.setLoggedInAgent(loggedInAgent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Insurance Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onclickmakeclaim(ActionEvent event) {
        String vehicleNumber = PCtxtVehiclenumber.getText();
        String policyNumber = PCtxtpolicyNumber.getText();
        LocalDate claimDate = PCTxtDate.getValue();
        String amount = PCtxtamount.getText();

        if (vehicleNumber.isEmpty() || policyNumber.isEmpty() || claimDate == null || amount.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all required fields: Vehicle Number, Policy Number, Date, and Amount!");
            return;
        }

        // Validate amount
        double claimAmount;
        try {
            claimAmount = Double.parseDouble(amount);
            if (claimAmount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Amount must be greater than 0!");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid amount!");
            return;
        }

        try (Connection conn = ConnectionDB.getConnection()) {
            // First verify policy exists and get policy_id
            String policySql = "SELECT ip.policy_id, v.registration_number FROM InsurancePolicy ip " +
                    "JOIN Vehicle v ON ip.vehicle_id = v.vehicle_id " +
                    "WHERE ip.policy_number = ? AND v.registration_number = ?";
            PreparedStatement policyStmt = conn.prepareStatement(policySql);
            policyStmt.setString(1, policyNumber);
            policyStmt.setString(2, vehicleNumber);
            ResultSet rs = policyStmt.executeQuery();

            int policyId = -1;
            if (rs.next()) {
                policyId = rs.getInt("policy_id");
            } else {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Policy not found! Please check Policy Number and Vehicle Number.");
                rs.close();
                policyStmt.close();
                return;
            }
            rs.close();
            policyStmt.close();

            // Insert claim
            String sql = "INSERT INTO Claim (policy_id, claim_date, claim_amount, status) VALUES (?, ?, ?, 'Pending')";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, policyId);
            pstmt.setDate(2, java.sql.Date.valueOf(claimDate));
            pstmt.setBigDecimal(3, new java.math.BigDecimal(claimAmount));

            int result = pstmt.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Claim submitted successfully! Claim ID will be generated automatically.");
                clearClaimFields();
                PClblClaimStatus.setText("Status: Submitted ✓");
            }
            pstmt.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to submit claim: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearPolicyFields() {
        PCtxtpolicynumber.clear();
        PCtxtVehicleNumber.clear();
        PCtxtinsurancecompany.clear();
        PCtxtStartDateP.setValue(null);
        PCtxtEndDateP.setValue(null);
        PCtxtCoverageDetails.clear();
    }

    private void clearClaimFields() {
        PCtxtClaimID.clear();
        PCtxtVehiclenumber.clear();
        PCtxtpolicyNumber.clear();
        PCTxtDate.setValue(null);
        PCtxtamount.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}