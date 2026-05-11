package com.example.vehicle_identification_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ViolationPage {

    @FXML
    private Button VPbtnAddViolation;

    @FXML
    private Button VPbtnReset;

    @FXML
    private Pane VPAllviolatonsCart;

    @FXML
    private Pane VPCartViolation;

    @FXML
    private Button VPbtnCustomerSupport;

    @FXML
    private Button VPbtnMainDashboard;

    @FXML
    private Button VPbtnReports;

    @FXML
    private Button VPbtnViolation;

    @FXML
    private Button VPbtnlogout;

    @FXML
    private TableColumn<ViolationData, Integer> VPcolumnID;

    @FXML
    private TableColumn<ViolationData, String> VPcolumnRegistration;

    @FXML
    private TableColumn<ViolationData, String> VPcolumnmake;

    @FXML
    private TableColumn<ViolationData, String> VPcolumnViolationtype;

    @FXML
    private TableColumn<ViolationData, LocalDate> VPcolumnviolatiomDate;

    @FXML
    private TableColumn<ViolationData, Double> VPcolumnfine;

    @FXML
    private TableColumn<ViolationData, String> VPcolumnStatus;

    @FXML
    private TableColumn<ViolationData, Button> VPcolumnAction;

    @FXML
    private Pane VPcustomerPane;

    @FXML
    private Label VPlblAllvioaltion;

    @FXML
    private Label VPlblBadgeID;

    @FXML
    private Label VPlblDescription;

    @FXML
    private Label VPlblHeader;

    @FXML
    private Label VPlblOfficername;

    @FXML
    private Label VPlblRegistrationNumber;

    @FXML
    private Label VPlblVehicleIdentification;

    @FXML
    private Label VPlblViolationDate;

    @FXML
    private Label VPlblViolationTime;

    @FXML
    private Label VPlblcustomer;

    @FXML
    private Label VPlbllocation;

    @FXML
    private Label VPlblname;

    @FXML
    private Label VPlblsystem;

    @FXML
    private Label VPlblwearehere;

    @FXML
    private TableView<ViolationData> VPtableview;

    @FXML
    private TextField VPtxtDescription;

    @FXML
    private TextField VPtxtFineamount;

    @FXML
    private TextField VPtxtLocation;

    @FXML
    private TextField VPtxtOfficername;

    @FXML
    private TextField VPtxtSearch;

    @FXML
    private DatePicker VPtxtViolationDate;

    @FXML
    private TextField VPtxtViolationTime;

    @FXML
    private TextField VPtxtViolationtype;

    @FXML
    private TextField VPtxtbadgeID;

    @FXML
    private TextField VPtxtregistrationNumber;

    private String loggedInOfficer = "";
    private ObservableList<ViolationData> violationsList = FXCollections.observableArrayList();

    public void setLoggedInOfficer(String username) {
        this.loggedInOfficer = username;
        VPlblname.setText(username);
        VPtxtOfficername.setText(username);
        loadAllViolations();
    }

    @FXML
    void onclickCustomerSupport(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Customer Support");
        alert.setHeaderText(null);
        alert.setContentText("Contact Support:\nEmergency: 10111\nEmail: police@vehicleid.com");
        alert.showAndWait();
    }

    @FXML
    void onclickDashboardVP(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/PolicePage.fxml"));
            Parent root = loader.load();
            PolicePage policePage = loader.getController();
            policePage.setLoggedInOfficer(loggedInOfficer);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Police Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onclickReportsVP(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/ReportPage.fxml"));
            Parent root = loader.load();
            ReportPage reportPage = loader.getController();
            reportPage.setLoggedInOfficer(loggedInOfficer);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Police Reports");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void OnclickAddViolation(ActionEvent event) {
        String registrationNumber = VPtxtregistrationNumber.getText().trim();
        String violationType = VPtxtViolationtype.getText().trim();
        LocalDate violationDate = VPtxtViolationDate.getValue();
        String violationTime = VPtxtViolationTime.getText().trim();
        String location = VPtxtLocation.getText().trim();
        String fineAmount = VPtxtFineamount.getText().trim();
        String officerName = VPtxtOfficername.getText().trim();
        String badgeId = VPtxtbadgeID.getText().trim();
        String description = VPtxtDescription.getText().trim();

        if (registrationNumber.isEmpty() || violationType.isEmpty() || violationDate == null || fineAmount.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please fill required fields: Registration Number, Violation Type, Date, and Fine Amount");
            return;
        }

        double fine;
        try {
            fine = Double.parseDouble(fineAmount);
            if (fine < 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Fine amount cannot be negative");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid fine amount");
            return;
        }

        try (Connection conn = ConnectionDB.getPoliceConnection()) {
            String vehicleSql = "SELECT vehicle_id FROM Vehicle WHERE registration_number = ?";
            PreparedStatement vehicleStmt = conn.prepareStatement(vehicleSql);
            vehicleStmt.setString(1, registrationNumber);
            ResultSet rs = vehicleStmt.executeQuery();

            int vehicleId;
            if (rs.next()) {
                vehicleId = rs.getInt("vehicle_id");
            } else {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Vehicle not found with registration number: " + registrationNumber);
                rs.close();
                vehicleStmt.close();
                return;
            }
            rs.close();
            vehicleStmt.close();

            String fullDescription = description;
            if (violationTime != null && !violationTime.isEmpty()) {
                fullDescription = "Time: " + violationTime + "\n" + (description.isEmpty() ? violationType : description);
            }

            String sql = "INSERT INTO Violation (vehicle_id, violation_date, violation_type, fine_amount, status, location, officer_name, description) " +
                    "VALUES (?, ?, ?, ?, 'Unpaid', ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, vehicleId);
            pstmt.setDate(2, java.sql.Date.valueOf(violationDate));
            pstmt.setString(3, violationType);
            pstmt.setBigDecimal(4, new java.math.BigDecimal(fine));
            pstmt.setString(5, location.isEmpty() ? null : location);
            pstmt.setString(6, officerName.isEmpty() ? loggedInOfficer : officerName);
            pstmt.setString(7, fullDescription);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Violation recorded successfully!");
                OnclickResetBtn(event);
                loadAllViolations();
            }
            pstmt.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to record violation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void OnclickResetBtn(ActionEvent event) {
        VPtxtregistrationNumber.clear();
        VPtxtViolationtype.clear();
        VPtxtViolationDate.setValue(null);
        VPtxtViolationTime.clear();
        VPtxtLocation.clear();
        VPtxtFineamount.clear();
        VPtxtbadgeID.clear();
        VPtxtDescription.clear();
        VPtxtOfficername.setText(loggedInOfficer);
    }

    @FXML
    void onclickViolationVP(ActionEvent event) {
        loadAllViolations();
        showAlert(Alert.AlertType.INFORMATION, "Violation Page", "Violation list refreshed");
    }

    @FXML
    void onclicklogoutVP(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/Main-menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onSearchViolations(ActionEvent event) {
        String searchText = VPtxtSearch.getText();
        if (!searchText.isEmpty()) {
            searchViolations(searchText);
        } else {
            loadAllViolations();
        }
    }

    private void loadAllViolations() {
        violationsList.clear();
        try (Connection conn = ConnectionDB.getPoliceConnection()) {
            String sql = "SELECT v.violation_id, v.violation_type, v.violation_date, v.fine_amount, v.status, v.location, v.officer_name, " +
                    "vh.registration_number, vh.make, vh.model " +
                    "FROM Violation v " +
                    "JOIN Vehicle vh ON v.vehicle_id = vh.vehicle_id " +
                    "ORDER BY v.violation_date DESC";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ViolationData violation = new ViolationData(
                        rs.getInt("violation_id"),
                        rs.getString("registration_number"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getString("violation_type"),
                        rs.getDate("violation_date") != null ? rs.getDate("violation_date").toLocalDate() : null,
                        rs.getDouble("fine_amount"),
                        rs.getString("status"),
                        rs.getString("location") != null ? rs.getString("location") : "N/A",
                        rs.getString("officer_name") != null ? rs.getString("officer_name") : "N/A"
                );
                violationsList.add(violation);
            }
            rs.close();
            pstmt.close();

            VPcolumnID.setCellValueFactory(new PropertyValueFactory<>("violationId"));
            VPcolumnRegistration.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
            VPcolumnmake.setCellValueFactory(new PropertyValueFactory<>("make"));
            VPcolumnViolationtype.setCellValueFactory(new PropertyValueFactory<>("violationType"));
            VPcolumnviolatiomDate.setCellValueFactory(new PropertyValueFactory<>("violationDate"));
            VPcolumnfine.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
            VPcolumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            VPtableview.setItems(violationsList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load violations: " + e.getMessage());
        }
    }

    private void searchViolations(String searchText) {
        violationsList.clear();
        try (Connection conn = ConnectionDB.getPoliceConnection()) {
            String sql = "SELECT v.violation_id, v.violation_type, v.violation_date, v.fine_amount, v.status, v.location, v.officer_name, " +
                    "vh.registration_number, vh.make, vh.model " +
                    "FROM Violation v " +
                    "JOIN Vehicle vh ON v.vehicle_id = vh.vehicle_id " +
                    "WHERE vh.registration_number ILIKE ? OR v.violation_type ILIKE ? OR v.status ILIKE ? " +
                    "ORDER BY v.violation_date DESC";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ViolationData violation = new ViolationData(
                        rs.getInt("violation_id"),
                        rs.getString("registration_number"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getString("violation_type"),
                        rs.getDate("violation_date") != null ? rs.getDate("violation_date").toLocalDate() : null,
                        rs.getDouble("fine_amount"),
                        rs.getString("status"),
                        rs.getString("location") != null ? rs.getString("location") : "N/A",
                        rs.getString("officer_name") != null ? rs.getString("officer_name") : "N/A"
                );
                violationsList.add(violation);
            }
            rs.close();
            pstmt.close();
            VPtableview.setItems(violationsList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}