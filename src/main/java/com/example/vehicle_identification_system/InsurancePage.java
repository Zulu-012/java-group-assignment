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

public class InsurancePage {

    @FXML
    private Pane IPApprovedclaimsCart;

    @FXML
    private Pane IPTotalpayoutCart;

    @FXML
    private Button IPbtnCustomerChat;

    @FXML
    private Button IPbtnMainDashboard;

    @FXML
    private Button IPbtnlogout;

    @FXML
    private Button IPbtnpolicies;

    @FXML
    private TableColumn<InsuranceData, LocalDate> IPcolumnDate;

    @FXML
    private TableColumn<InsuranceData, String> IPcolumnPolicyNumber;

    @FXML
    private TableColumn<InsuranceData, String> IPcolumnVehicleNumber;

    @FXML
    private TableColumn<InsuranceData, Double> IPcolumnClaimAmount;

    @FXML
    private TableColumn<InsuranceData, LocalDate> IPcolumnValidtill;

    @FXML
    private TableColumn<InsuranceData, String> IPcolumnCustomer;

    @FXML
    private TableColumn<InsuranceData, String> IPcolumnStatus;

    @FXML
    private TableColumn<InsuranceData, Button> IPcolumnAction;

    @FXML
    private Label IPlblActivePolicies;

    @FXML
    private Label IPlblApprovedClaims;

    @FXML
    private Label IPlblNumberOfActivePolices;

    @FXML
    private Label IPlblRecentClaims;

    @FXML
    private Label IPlblSystem;

    @FXML
    private Label IPlblTotalPayout;

    @FXML
    private Label IPlblVehicleIdetification;

    @FXML
    private Label IPlblWesecure;

    @FXML
    private Label IPlblcustomer;

    @FXML
    private Label IPlblcustomername;

    @FXML
    private Label IPlblnumberofClaims;

    @FXML
    private Label IPlblnumberofactivepolicies;

    @FXML
    private Label IPlblnumberofclaims;

    @FXML
    private Label IPlblnumberoftotalPayout;

    @FXML
    private Label IPlblpendingClaims;

    @FXML
    private Label IPlblpolicy;

    @FXML
    private Label IPlblrole;

    @FXML
    private Label IPlblsecure;

    @FXML
    private Label IPlbltotalActivepolicies;

    @FXML
    private Pane IPlbltotalActivepoliciesthismonth;

    @FXML
    private Label IPlblweareheretoassistyou;

    @FXML
    private Label IPlblweensure;

    @FXML
    private Pane IPpanecustomer;

    @FXML
    private Pane IPpendingClaimsCart;

    @FXML
    private Pane IPsidenavigation;

    @FXML
    private TableView<InsuranceData> IPtableview;

    @FXML
    private TextField IPtxtSearch;

    @FXML
    private Pane IpActivePolicyCart;

    @FXML
    private ImageView Ipimagebackground;

    @FXML
    private Pane paneClaims;

    @FXML
    private Pane root;

    @FXML
    private ScrollBar scrollbar;

    private String loggedInAgent = "";
    private ObservableList<InsuranceData> policiesList = FXCollections.observableArrayList();

    public void setLoggedInAgent(String username) {
        this.loggedInAgent = username;
        IPlblcustomername.setText(username);
        IPlblrole.setText("Insurance Agent");
        loadDashboardStats();
        loadRecentPoliciesAndClaims();
    }

    private void loadDashboardStats() {
        try (Connection conn = ConnectionDB.getInsuranceConnection()) {
            // Total Active Policies
            String activePoliciesSql = "SELECT COUNT(*) FROM InsurancePolicy WHERE end_date >= CURRENT_DATE";
            PreparedStatement pstmt = conn.prepareStatement(activePoliciesSql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                IPlblnumberofactivepolicies.setText(String.valueOf(count));
                IPlblNumberOfActivePolices.setText(String.valueOf(count));
                IPlbltotalActivepolicies.setText("Total Active: " + count);
            }
            rs.close();
            pstmt.close();

            // Pending Claims
            String pendingClaimsSql = "SELECT COUNT(*) FROM Claim WHERE status = 'Pending'";
            pstmt = conn.prepareStatement(pendingClaimsSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                IPlblnumberofClaims.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

            // Approved Claims
            String approvedClaimsSql = "SELECT COUNT(*) FROM Claim WHERE status = 'Approved'";
            pstmt = conn.prepareStatement(approvedClaimsSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                IPlblnumberofclaims.setText(String.valueOf(rs.getInt(1)));
                IPlblApprovedClaims.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

            // Total Payout
            String totalPayoutSql = "SELECT COALESCE(SUM(claim_amount), 0) FROM Claim WHERE status = 'Approved'";
            pstmt = conn.prepareStatement(totalPayoutSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble(1);
                IPlblnumberoftotalPayout.setText("R " + String.format("%,.2f", total));
                IPlblTotalPayout.setText("R " + String.format("%,.2f", total));
            }
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            setDefaultStats();
        }
    }

    private void setDefaultStats() {
        IPlblnumberofactivepolicies.setText("0");
        IPlblNumberOfActivePolices.setText("0");
        IPlblnumberofClaims.setText("0");
        IPlblnumberofclaims.setText("0");
        IPlblnumberoftotalPayout.setText("R 0.00");
        IPlblTotalPayout.setText("R 0.00");
    }

    private void loadRecentPoliciesAndClaims() {
        policiesList.clear();
        try (Connection conn = ConnectionDB.getInsuranceConnection()) {
            // Query to get combined policy and claim data
            String sql = "SELECT ip.policy_id, ip.policy_number, ip.start_date, ip.end_date, ip.insurance_company, " +
                    "v.registration_number as vehicle_number, c.name as customer_name, " +
                    "cl.claim_id, cl.claim_amount, cl.status as claim_status, cl.claim_date " +
                    "FROM InsurancePolicy ip " +
                    "JOIN Vehicle v ON ip.vehicle_id = v.vehicle_id " +
                    "JOIN Customer c ON v.owner_id = c.customer_id " +
                    "LEFT JOIN Claim cl ON ip.policy_id = cl.policy_id " +
                    "ORDER BY ip.start_date DESC LIMIT 20";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                InsuranceData data = new InsuranceData(
                        rs.getInt("policy_id"),
                        rs.getString("policy_number"),
                        rs.getString("vehicle_number"),
                        rs.getString("customer_name"),
                        rs.getDate("start_date") != null ? rs.getDate("start_date").toLocalDate() : null,
                        rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null,
                        rs.getDouble("claim_amount"),
                        rs.getString("claim_status") != null ? rs.getString("claim_status") : "Active",
                        rs.getString("insurance_company")
                );
                policiesList.add(data);
            }
            rs.close();
            pstmt.close();

            // Set up table columns
            IPcolumnDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
            IPcolumnPolicyNumber.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
            IPcolumnVehicleNumber.setCellValueFactory(new PropertyValueFactory<>("vehicleNumber"));
            IPcolumnClaimAmount.setCellValueFactory(new PropertyValueFactory<>("claimAmount"));
            IPcolumnValidtill.setCellValueFactory(new PropertyValueFactory<>("endDate"));
            IPcolumnCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            IPcolumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            IPtableview.setItems(policiesList);
            IPtableview.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load policies: " + e.getMessage());
        }
    }

    private void searchPolicies(String searchText) {
        policiesList.clear();
        try (Connection conn = ConnectionDB.getInsuranceConnection()) {
            String sql = "SELECT ip.policy_id, ip.policy_number, ip.start_date, ip.end_date, ip.insurance_company, " +
                    "v.registration_number as vehicle_number, c.name as customer_name, " +
                    "cl.claim_id, cl.claim_amount, cl.status as claim_status, cl.claim_date " +
                    "FROM InsurancePolicy ip " +
                    "JOIN Vehicle v ON ip.vehicle_id = v.vehicle_id " +
                    "JOIN Customer c ON v.owner_id = c.customer_id " +
                    "LEFT JOIN Claim cl ON ip.policy_id = cl.policy_id " +
                    "WHERE v.registration_number ILIKE ? OR c.name ILIKE ? OR ip.policy_number ILIKE ? " +
                    "ORDER BY ip.start_date DESC";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                InsuranceData data = new InsuranceData(
                        rs.getInt("policy_id"),
                        rs.getString("policy_number"),
                        rs.getString("vehicle_number"),
                        rs.getString("customer_name"),
                        rs.getDate("start_date") != null ? rs.getDate("start_date").toLocalDate() : null,
                        rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null,
                        rs.getDouble("claim_amount"),
                        rs.getString("claim_status") != null ? rs.getString("claim_status") : "Active",
                        rs.getString("insurance_company")
                );
                policiesList.add(data);
            }
            rs.close();
            pstmt.close();
            IPtableview.setItems(policiesList);
            IPtableview.refresh();

            if (policiesList.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Results", "No policies found matching: " + searchText);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to search policies: " + e.getMessage());
        }
    }

    @FXML
    void onclickDashboardIP(ActionEvent event) {
        loadDashboardStats();
        loadRecentPoliciesAndClaims();
        showAlert(Alert.AlertType.INFORMATION, "Dashboard", "Dashboard refreshed");
    }

    @FXML
    void onclicklogoutIP(ActionEvent event) {
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
    void onclickpoliceiesandClaims(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/Policies_and_Claims.fxml"));
            Parent root = loader.load();
            PoliciesAndClaims policiesAndClaims = loader.getController();
            policiesAndClaims.setLoggedInAgent(loggedInAgent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Policies and Claims");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Policies and Claims page");
        }
    }

    @FXML
    void onclickCustomerChat(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Customer Support",
                "Contact Insurance Support:\nPhone: 0860 123 456\nEmail: insurance@vehicleid.com\nHours: Mon-Fri 8am-5pm");
    }

    @FXML
    void onSearchPolicies(ActionEvent event) {
        String searchText = IPtxtSearch.getText();
        if (!searchText.isEmpty()) {
            searchPolicies(searchText);
        } else {
            loadRecentPoliciesAndClaims();
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