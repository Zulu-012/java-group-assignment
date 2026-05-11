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

public class MyVehiclePage {

    @FXML
    private Pane MPActiveInsuranceCart;

    @FXML
    private Button MPbtnBuycar;

    @FXML
    private Button MPbtncustomer;

    @FXML
    private Button MPbtnlogout;

    @FXML
    private Button MPbtnmyVehicle;

    @FXML
    private Button MPbtnsellcar;

    @FXML
    private TableColumn<VehicleData, Button> MPcolumnAction;

    @FXML
    private TableColumn<VehicleData, String> MPcolumnInsuranceStatus;

    @FXML
    private TableColumn<VehicleData, String> MPcolumnMake;

    @FXML
    private TableColumn<VehicleData, String> MPcolumnModel;

    @FXML
    private TableColumn<VehicleData, String> MPcolumnRegistrationnumber;

    @FXML
    private TableColumn<VehicleData, String> MPcolumnfueltype;

    @FXML
    private TableColumn<VehicleData, Integer> MPcolumnid;

    @FXML
    private TableColumn<VehicleData, String> MPcolumnlastservice;

    @FXML
    private TableColumn<VehicleData, Integer> MPcolumnyear;

    @FXML
    private Pane MPcustomercart;

    @FXML
    private Label MPlblActiveInsurance;

    @FXML
    private Label MPlblcustomerrole;

    @FXML
    private Label MPlblheader;

    @FXML
    private Label MPlblname;

    @FXML
    private Label MPlblneedhelp;

    @FXML
    private Label MPlblnumberofActiveInsurace;

    @FXML
    private Label MPlblnumberoftotalqueries;

    @FXML
    private Label MPlblnumberoftotalservices;

    @FXML
    private Label MPlblnumberoftotalvehicles;

    @FXML
    private Label MPlblsystem;

    @FXML
    private Label MPlbltotalQueries;

    @FXML
    private Label MPlbltotalservices;

    @FXML
    private Label MPlbltotalvehicle;

    @FXML
    private Label MPlblvehicleidentification;

    @FXML
    private Label MPlblwearehertoassistyou;

    @FXML
    private Pane MProlecart;

    @FXML
    private Pane MPsidenavigation;

    @FXML
    private TableView<VehicleData> MPtableview;

    @FXML
    private Pane MPtotalQueriesCart;

    @FXML
    private Pane MPtotalServicescart;

    @FXML
    private TextField MPtxtSearch;

    @FXML
    private Pane MPvehicleCArt;

    @FXML
    private Pane root;

    @FXML
    private ScrollBar scrollbar;

    private String loggedInUser = "";
    private int customerId = 0;
    private ObservableList<VehicleData> userVehiclesList = FXCollections.observableArrayList();

    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
        MPlblname.setText(username);
        getCustomerId();
        loadDashboardStats();
        loadUserVehicles();
    }

    private void getCustomerId() {
        try (Connection conn = ConnectionDB.getConnection()) {
            String sql = "SELECT customer_id FROM Customer WHERE name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, loggedInUser);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                customerId = rs.getInt("customer_id");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDashboardStats() {
        try (Connection conn = ConnectionDB.getConnection()) {
            // Count total vehicles owned by user
            String vehicleSql = "SELECT COUNT(*) FROM Vehicle WHERE owner_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(vehicleSql);
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                MPlblnumberoftotalvehicles.setText(String.valueOf(count));
            }
            rs.close();
            pstmt.close();

            // Count active insurance policies
            String insuranceSql = "SELECT COUNT(*) FROM InsurancePolicy ip JOIN Vehicle v ON ip.vehicle_id = v.vehicle_id WHERE v.owner_id = ? AND ip.end_date >= CURRENT_DATE";
            pstmt = conn.prepareStatement(insuranceSql);
            pstmt.setInt(1, customerId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                MPlblnumberofActiveInsurace.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

            // Count total services
            String serviceSql = "SELECT COUNT(*) FROM ServiceRecord sr JOIN Vehicle v ON sr.vehicle_id = v.vehicle_id WHERE v.owner_id = ?";
            pstmt = conn.prepareStatement(serviceSql);
            pstmt.setInt(1, customerId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                MPlblnumberoftotalservices.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

            // Count total queries
            String querySql = "SELECT COUNT(*) FROM CustomerQuery WHERE customer_id = ?";
            pstmt = conn.prepareStatement(querySql);
            pstmt.setInt(1, customerId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                MPlblnumberoftotalqueries.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            MPlblnumberoftotalvehicles.setText("0");
            MPlblnumberofActiveInsurace.setText("0");
            MPlblnumberoftotalservices.setText("0");
            MPlblnumberoftotalqueries.setText("0");
        }
    }

    private void loadUserVehicles() {
        userVehiclesList.clear();
        try (Connection conn = ConnectionDB.getConnection()) {
            String sql = "SELECT v.vehicle_id, v.registration_number, v.make, v.model, v.year, v.fuel_type, v.mileage, v.price, v.status, " +
                    "CASE WHEN ip.policy_id IS NOT NULL AND ip.end_date >= CURRENT_DATE THEN 'Active' ELSE 'No Insurance' END as insurance_status " +
                    "FROM Vehicle v " +
                    "LEFT JOIN InsurancePolicy ip ON v.vehicle_id = ip.vehicle_id AND ip.end_date >= CURRENT_DATE " +
                    "WHERE v.owner_id = ? " +
                    "ORDER BY v.vehicle_id";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                VehicleData vehicle = new VehicleData(
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getString("fuel_type"),
                        rs.getInt("mileage"),
                        rs.getDouble("price"),
                        rs.getString("insurance_status"),
                        rs.getString("status")
                );
                userVehiclesList.add(vehicle);
            }
            rs.close();
            pstmt.close();

            // Set up table columns
            MPcolumnid.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
            MPcolumnRegistrationnumber.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
            MPcolumnMake.setCellValueFactory(new PropertyValueFactory<>("make"));
            MPcolumnModel.setCellValueFactory(new PropertyValueFactory<>("model"));
            MPcolumnyear.setCellValueFactory(new PropertyValueFactory<>("year"));
            MPcolumnfueltype.setCellValueFactory(new PropertyValueFactory<>("fuelType"));
            MPcolumnInsuranceStatus.setCellValueFactory(new PropertyValueFactory<>("insuranceStatus"));

            MPtableview.setItems(userVehiclesList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load your vehicles: " + e.getMessage());
        }
    }

    @FXML
    void onclickLogoutMp(ActionEvent event) {
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
    void onclickMyvehicleMp(ActionEvent event) {
        loadUserVehicles();
        showAlert(Alert.AlertType.INFORMATION, "My Vehicles", "Vehicle list refreshed");
    }

    @FXML
    void onclickbuycarMP(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/BuyCarPage.fxml"));
            Parent root = loader.load();
            BuyCarPage buyCarPage = loader.getController();
            buyCarPage.setLoggedInUser(loggedInUser);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Buy a Car");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onclickcustomerMP(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Customer Support", "Contact us: support@vehicleid.com\nPhone: +27 123 456 789");
    }

    @FXML
    void onclicksellCarMP(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/SellCarPage.fxml"));
            Parent root = loader.load();
            SellCarPage sellCarPage = loader.getController();
            sellCarPage.setLoggedInUser(loggedInUser);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sell a Car");
            stage.show();
        } catch (IOException e) {
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