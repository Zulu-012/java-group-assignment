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

public class ReportPage {

    @FXML
    private Pane RPAllreportsCart;

    @FXML
    private Pane RPReportsPane;

    @FXML
    private Button RPbtnCustomerSupport;

    @FXML
    private Button RPbtnlogoutRP;

    @FXML
    private Button RPbtnmainDashboard;

    @FXML
    private Button RPbtnreports;

    @FXML
    private Button RPbtnviolation;

    @FXML
    private TableColumn<PoliceReportData, Integer> RPcolumnid;

    @FXML
    private TableColumn<PoliceReportData, String> RPcolumnreportnumber;

    @FXML
    private TableColumn<PoliceReportData, String> RPcolumnreporttype;

    @FXML
    private TableColumn<PoliceReportData, LocalDate> RPcolumnreportdate;

    @FXML
    private TableColumn<PoliceReportData, String> RPcolumnlocation;

    @FXML
    private TableColumn<PoliceReportData, String> RPcolumnoffivername;

    @FXML
    private TableColumn<PoliceReportData, String> RPcolumnstatus;

    @FXML
    private TableColumn<PoliceReportData, Button> RPcolumnAction;

    @FXML
    private Pane RPcustomerCart;

    @FXML
    private Label RPlblAllreports;

    @FXML
    private Label RPlblBadgeId;

    @FXML
    private Label RPlblDescription;

    @FXML
    private Label RPlblReports;

    @FXML
    private Label RPlblReporttime;

    @FXML
    private Label RPlblSystem;

    @FXML
    private Label RPlblWeareheretoassityou;

    @FXML
    private Label RPlblcustomer;

    @FXML
    private Label RPlbllocation;

    @FXML
    private Label RPlblname;

    @FXML
    private Label RPlblofficerName;

    @FXML
    private Label RPlblreportdate;

    @FXML
    private Label RPlblreporttype;

    @FXML
    private Label RPlblroleCustomer;

    @FXML
    private Label RPlbltitleSummary;

    @FXML
    private Label RPlblvehicleidentification;

    @FXML
    private Pane RProleCart;

    @FXML
    private Pane RPsidenavigation;

    @FXML
    private TableView<PoliceReportData> RPtableview;

    @FXML
    private TextField RPtxtBadgeID;

    @FXML
    private DatePicker RPtxtDate;

    @FXML
    private TextField RPtxtDescription;

    @FXML
    private TextField RPtxtLocation;

    @FXML
    private TextField RPtxtOfficername;

    @FXML
    private TextField RPtxtReorttime;

    @FXML
    private TextField RPtxtSearch;

    @FXML
    private TextField RPtxtTitleSummary;

    @FXML
    private TextField RPtxtreporttype;

    @FXML
    private Pane root;

    @FXML
    private Button RPbtnAddreport;

    @FXML
    private Button RPbtnReset;

    @FXML
    private TextField RPtxtRegistrationNumber;

    private String loggedInOfficer = "";
    private ObservableList<PoliceReportData> reportsList = FXCollections.observableArrayList();

    public void setLoggedInOfficer(String username) {
        this.loggedInOfficer = username;
        RPlblname.setText(username);
        RPtxtOfficername.setText(username);
        loadAllReports();
    }

    @FXML
    void onclickCustomerSupportRP(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Customer Support");
        alert.setHeaderText(null);
        alert.setContentText("Contact Support:\nEmergency: 10111\nEmail: police@vehicleid.com");
        alert.showAndWait();
    }

    @FXML
    void onclickDashboardRP(ActionEvent event) {
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
    void onclickAddreport(ActionEvent event) {
        String registrationNumber = RPtxtRegistrationNumber != null ? RPtxtRegistrationNumber.getText().trim() : "";
        String reportType = RPtxtreporttype.getText().trim();
        LocalDate reportDate = RPtxtDate.getValue();
        String reportTime = RPtxtReorttime.getText().trim();
        String location = RPtxtLocation.getText().trim();
        String summary = RPtxtTitleSummary.getText().trim();
        String officerName = RPtxtOfficername.getText().trim();
        String badgeId = RPtxtBadgeID.getText().trim();
        String description = RPtxtDescription.getText().trim();

        // Validation
        if (reportType.isEmpty() || reportDate == null || location.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please fill required fields: Report Type, Date, and Location");
            return;
        }

        try (Connection conn = ConnectionDB.getPoliceConnection()) {
            // Combine description
            String fullDescription = description;
            if (reportTime != null && !reportTime.isEmpty()) {
                fullDescription = "Time: " + reportTime + "\n" + (description.isEmpty() ? summary : description);
            } else if (!summary.isEmpty()) {
                fullDescription = summary + "\n" + description;
            }

            // Check if vehicle exists with given registration number
            Integer vehicleId = null;
            String validRegistration = null;

            if (registrationNumber != null && !registrationNumber.isEmpty()) {
                String vehicleSql = "SELECT vehicle_id, registration_number FROM Vehicle WHERE registration_number = ?";
                PreparedStatement vehicleStmt = conn.prepareStatement(vehicleSql);
                vehicleStmt.setString(1, registrationNumber);
                ResultSet rs = vehicleStmt.executeQuery();
                if (rs.next()) {
                    vehicleId = rs.getInt("vehicle_id");
                    validRegistration = rs.getString("registration_number");
                } else {
                    // Vehicle not found - ask user if they want to proceed without vehicle
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Vehicle Not Found");
                    confirmAlert.setHeaderText("Registration number not found: " + registrationNumber);
                    confirmAlert.setContentText("Do you want to create a report without linking to a vehicle?");

                    if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                        rs.close();
                        vehicleStmt.close();
                        return;
                    }
                }
                rs.close();
                vehicleStmt.close();
            }

            // Insert report - vehicle_id is now optional
            String sql = "INSERT INTO PoliceReport (report_date, report_type, description, officer_name, location, status, registration_number, vehicle_id) " +
                    "VALUES (?, ?, ?, ?, ?, 'Active', ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(reportDate));
            pstmt.setString(2, reportType);
            pstmt.setString(3, fullDescription);
            pstmt.setString(4, officerName.isEmpty() ? loggedInOfficer : officerName);
            pstmt.setString(5, location);
            pstmt.setString(6, registrationNumber.isEmpty() ? null : registrationNumber);

            // Handle vehicle_id (can be null)
            if (vehicleId != null) {
                pstmt.setInt(7, vehicleId);
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }

            int result = pstmt.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Report submitted successfully!");
                onclickRPReset(event);
                loadAllReports();
            }
            pstmt.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to submit report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onclickRPReset(ActionEvent event) {
        if (RPtxtRegistrationNumber != null) RPtxtRegistrationNumber.clear();
        RPtxtreporttype.clear();
        RPtxtDate.setValue(null);
        RPtxtReorttime.clear();
        RPtxtLocation.clear();
        RPtxtTitleSummary.clear();
        RPtxtBadgeID.clear();
        RPtxtDescription.clear();
        RPtxtOfficername.setText(loggedInOfficer);
    }

    @FXML
    void onclickReportsRP(ActionEvent event) {
        loadAllReports();
        showAlert(Alert.AlertType.INFORMATION, "Reports Page", "Reports list refreshed");
    }

    @FXML
    void onclickViolationRP(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/ViolationPage.fxml"));
            Parent root = loader.load();
            ViolationPage violationPage = loader.getController();
            violationPage.setLoggedInOfficer(loggedInOfficer);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Violation Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onclicklogoutRP(ActionEvent event) {
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
    void onSearchReports(ActionEvent event) {
        String searchText = RPtxtSearch.getText();
        if (!searchText.isEmpty()) {
            searchReports(searchText);
        } else {
            loadAllReports();
        }
    }

    private void loadAllReports() {
        reportsList.clear();
        try (Connection conn = ConnectionDB.getPoliceConnection()) {
            String sql = "SELECT report_id, registration_number, report_type, report_date, location, officer_name, status, description " +
                    "FROM PoliceReport ORDER BY report_date DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PoliceReportData report = new PoliceReportData(
                        rs.getInt("report_id"),
                        rs.getString("registration_number") != null ? rs.getString("registration_number") : "N/A",
                        rs.getString("report_type"),
                        rs.getDate("report_date") != null ? rs.getDate("report_date").toLocalDate() : null,
                        rs.getString("location") != null ? rs.getString("location") : "N/A",
                        rs.getString("officer_name"),
                        rs.getString("status") != null ? rs.getString("status") : "Active",
                        rs.getString("description")
                );
                reportsList.add(report);
            }
            rs.close();
            pstmt.close();

            // Set up table columns
            RPcolumnid.setCellValueFactory(new PropertyValueFactory<>("reportId"));
            RPcolumnreportnumber.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
            RPcolumnreporttype.setCellValueFactory(new PropertyValueFactory<>("reportType"));
            RPcolumnreportdate.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
            RPcolumnlocation.setCellValueFactory(new PropertyValueFactory<>("location"));
            RPcolumnoffivername.setCellValueFactory(new PropertyValueFactory<>("officerName"));
            RPcolumnstatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            RPtableview.setItems(reportsList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load reports: " + e.getMessage());
        }
    }

    private void searchReports(String searchText) {
        reportsList.clear();
        try (Connection conn = ConnectionDB.getPoliceConnection()) {
            String sql = "SELECT report_id, registration_number, report_type, report_date, location, officer_name, status, description " +
                    "FROM PoliceReport WHERE registration_number ILIKE ? OR report_type ILIKE ? OR officer_name ILIKE ? " +
                    "ORDER BY report_date DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PoliceReportData report = new PoliceReportData(
                        rs.getInt("report_id"),
                        rs.getString("registration_number") != null ? rs.getString("registration_number") : "N/A",
                        rs.getString("report_type"),
                        rs.getDate("report_date") != null ? rs.getDate("report_date").toLocalDate() : null,
                        rs.getString("location") != null ? rs.getString("location") : "N/A",
                        rs.getString("officer_name"),
                        rs.getString("status") != null ? rs.getString("status") : "Active",
                        rs.getString("description")
                );
                reportsList.add(report);
            }
            rs.close();
            pstmt.close();
            RPtableview.setItems(reportsList);

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