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
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PolicePage {

    @FXML
    private Pane PPActiveViolationsCart;

    @FXML
    private Button PPbtnMainDashboard;

    @FXML
    private Button PPbtnReports;

    @FXML
    private Button PPbtnViolations;

    @FXML
    private Button PPbtncustomersupport;

    @FXML
    private Button PPbtnlogout;

    @FXML
    private TableColumn<PoliceReportData, Integer> PPcolumnID;

    @FXML
    private TableColumn<PoliceReportData, String> PPcolumnRegistrationnumber;

    @FXML
    private TableColumn<PoliceReportData, String> PPcolumnMake;

    @FXML
    private TableColumn<PoliceReportData, String> PPcolumnmodel;

    @FXML
    private TableColumn<PoliceReportData, Integer> PPcolumnyear;

    @FXML
    private TableColumn<PoliceReportData, String> PPcolumnOwmername;

    @FXML
    private TableColumn<PoliceReportData, String> PPcolumnViolationID;

    @FXML
    private TableColumn<PoliceReportData, String> PPcolumnstatus;

    @FXML
    private TableColumn<PoliceReportData, Button> PPcolumnAction;

    @FXML
    private Pane PPcustomerCart;

    @FXML
    private ImageView PPimagebackground;

    @FXML
    private Label PPlblAccidentREports;

    @FXML
    private Pane PPlblAccidentReportsCart;

    @FXML
    private Label PPlblActiveViolations;

    @FXML
    private Label PPlblNumberofAccidents;

    @FXML
    private Label PPlblNumberofVehicles;

    @FXML
    private Label PPlblbylaw;

    @FXML
    private Label PPlblcustomer;

    @FXML
    private Label PPlblcustomername;

    @FXML
    private Label PPlblnumberofViolations;

    @FXML
    private Label PPlblnumberofvehicleschecked;

    @FXML
    private Label PPlblrcentreports;

    @FXML
    private Label PPlblstolenVehicles;

    @FXML
    private Label PPlblsystem;

    @FXML
    private Label PPlbltotalvehiclechecked;

    @FXML
    private Label PPlbltrsck;

    @FXML
    private Label PPlblvehicleidentification;

    @FXML
    private Label PPlblwesecure;

    @FXML
    private Label PPlblweserve;

    @FXML
    private Pane PPreportscart;

    @FXML
    private Pane PPsidenavigation;

    @FXML
    private Pane PPstolenVeheclesCart;

    @FXML
    private TableView<PoliceReportData> PPtableview;

    @FXML
    private Pane PPtotalvehiclecart;

    @FXML
    private TextField PPtxtSearch;

    @FXML
    private Pane root;

    private String loggedInOfficer = "";
    private ObservableList<PoliceReportData> recentReportsList = FXCollections.observableArrayList();

    public void setLoggedInOfficer(String username) {
        this.loggedInOfficer = username;
        PPlblcustomername.setText(username);
        loadDashboardStats();
        loadRecentReports();
    }

    private void loadDashboardStats() {
        try (Connection conn = ConnectionDB.getPoliceConnection()) {
            // Total vehicles checked (count of police reports)
            String vehicleSql = "SELECT COUNT(*) FROM PoliceReport";
            PreparedStatement pstmt = conn.prepareStatement(vehicleSql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                PPlblnumberofvehicleschecked.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

            // Active violations (unpaid violations)
            String violationSql = "SELECT COUNT(*) FROM Violation WHERE status = 'Unpaid'";
            pstmt = conn.prepareStatement(violationSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                PPlblnumberofViolations.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

            // Stolen vehicles
            String stolenSql = "SELECT COUNT(*) FROM PoliceReport WHERE report_type = 'Theft' OR report_type = 'Stolen Vehicle'";
            pstmt = conn.prepareStatement(stolenSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                PPlblNumberofVehicles.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

            // Accident reports
            String accidentSql = "SELECT COUNT(*) FROM PoliceReport WHERE report_type = 'Accident'";
            pstmt = conn.prepareStatement(accidentSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                PPlblNumberofAccidents.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            setDefaultStats();
        }
    }

    private void setDefaultStats() {
        PPlblnumberofvehicleschecked.setText("0");
        PPlblnumberofViolations.setText("0");
        PPlblNumberofVehicles.setText("0");
        PPlblNumberofAccidents.setText("0");
    }

    private void loadRecentReports() {
        recentReportsList.clear();
        try (Connection conn = ConnectionDB.getPoliceConnection()) {
            String sql = "SELECT pr.report_id, pr.report_type, pr.report_date, pr.description, pr.officer_name, pr.location, pr.status, " +
                    "v.registration_number, v.make, v.model, v.year, c.name as owner_name " +
                    "FROM PoliceReport pr " +
                    "LEFT JOIN Vehicle v ON pr.vehicle_id = v.vehicle_id " +
                    "LEFT JOIN Customer c ON v.owner_id = c.customer_id " +
                    "ORDER BY pr.report_date DESC LIMIT 20";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PoliceReportData report = new PoliceReportData(
                        rs.getInt("report_id"),
                        rs.getString("registration_number") != null ? rs.getString("registration_number") : "N/A",
                        rs.getString("report_type") != null ? rs.getString("report_type") : "N/A",
                        rs.getDate("report_date") != null ? rs.getDate("report_date").toLocalDate() : LocalDate.now(),
                        rs.getString("location") != null ? rs.getString("location") : "N/A",
                        rs.getString("officer_name") != null ? rs.getString("officer_name") : "N/A",
                        rs.getString("status") != null ? rs.getString("status") : "Active",
                        rs.getString("description") != null ? rs.getString("description") : "No description",
                        rs.getString("make") != null ? rs.getString("make") : "N/A",
                        rs.getString("model") != null ? rs.getString("model") : "N/A",
                        rs.getInt("year") != 0 ? rs.getInt("year") : 0,
                        rs.getString("owner_name") != null ? rs.getString("owner_name") : "N/A"
                );
                recentReportsList.add(report);
            }
            rs.close();
            pstmt.close();

            // Set up all table columns properly
            PPcolumnID.setCellValueFactory(new PropertyValueFactory<>("reportId"));
            PPcolumnRegistrationnumber.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
            PPcolumnMake.setCellValueFactory(new PropertyValueFactory<>("make"));
            PPcolumnmodel.setCellValueFactory(new PropertyValueFactory<>("model"));
            PPcolumnyear.setCellValueFactory(new PropertyValueFactory<>("year"));
            PPcolumnOwmername.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
            PPcolumnViolationID.setCellValueFactory(new PropertyValueFactory<>("reportId"));
            PPcolumnstatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            PPtableview.setItems(recentReportsList);
            PPtableview.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load reports: " + e.getMessage());
        }
    }

    private void searchReports(String searchText) {
        recentReportsList.clear();
        try (Connection conn = ConnectionDB.getPoliceConnection()) {
            String sql = "SELECT pr.report_id, pr.report_type, pr.report_date, pr.description, pr.officer_name, pr.location, pr.status, " +
                    "v.registration_number, v.make, v.model, v.year, c.name as owner_name " +
                    "FROM PoliceReport pr " +
                    "LEFT JOIN Vehicle v ON pr.vehicle_id = v.vehicle_id " +
                    "LEFT JOIN Customer c ON v.owner_id = c.customer_id " +
                    "WHERE v.registration_number ILIKE ? OR pr.report_type ILIKE ? OR pr.officer_name ILIKE ? " +
                    "ORDER BY pr.report_date DESC";

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
                        rs.getString("report_type") != null ? rs.getString("report_type") : "N/A",
                        rs.getDate("report_date") != null ? rs.getDate("report_date").toLocalDate() : LocalDate.now(),
                        rs.getString("location") != null ? rs.getString("location") : "N/A",
                        rs.getString("officer_name") != null ? rs.getString("officer_name") : "N/A",
                        rs.getString("status") != null ? rs.getString("status") : "Active",
                        rs.getString("description") != null ? rs.getString("description") : "No description",
                        rs.getString("make") != null ? rs.getString("make") : "N/A",
                        rs.getString("model") != null ? rs.getString("model") : "N/A",
                        rs.getInt("year") != 0 ? rs.getInt("year") : 0,
                        rs.getString("owner_name") != null ? rs.getString("owner_name") : "N/A"
                );
                recentReportsList.add(report);
            }
            rs.close();
            pstmt.close();
            PPtableview.setItems(recentReportsList);
            PPtableview.refresh();

            if (recentReportsList.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Results", "No reports found matching: " + searchText);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to search reports: " + e.getMessage());
        }
    }

    @FXML
    void onclickCustomerPP(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Customer Support",
                "Contact Police Support:\nEmergency: 10111\nNon-Emergency: 0860 010 111\nEmail: police@vehicleid.com");
    }

    @FXML
    void onclickLogoutPP(ActionEvent event) {
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
    void onclickReportsPP(ActionEvent event) {
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Reports page");
        }
    }

    @FXML
    void onclickViolationsPP(ActionEvent event) {
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Violation page");
        }
    }

    @FXML
    void onclickdashboardPP(ActionEvent event) {
        loadDashboardStats();
        loadRecentReports();
        showAlert(Alert.AlertType.INFORMATION, "Dashboard", "Dashboard refreshed");
    }

    @FXML
    void onSearchReports(ActionEvent event) {
        String searchText = PPtxtSearch.getText();
        if (!searchText.isEmpty()) {
            searchReports(searchText);
        } else {
            loadRecentReports();
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