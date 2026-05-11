package com.example.vehicle_identification_system;

import javafx.animation.FadeTransition;
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
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerPage {

    @FXML
    private Pane CPsidenavigation;

    @FXML
    private Button CSbtnbuycar;

    @FXML
    private Label CSlblSystem;

    @FXML
    private Label CSlbvehicleId;

    @FXML
    private Button CSmyvehicle;

    @FXML
    private Button CSsellcar;

    @FXML
    private Button btnCustomerSupport;

    @FXML
    private Button btnSearchVehicleCS;

    @FXML
    private Button btnlogoutCS;

    @FXML
    private TableColumn<VehicleData, Integer> columnIDCS;

    @FXML
    private TableColumn<VehicleData, String> colunmRegistrationNumberCS;

    @FXML
    private TableColumn<VehicleData, String> columnMakeCS;

    @FXML
    private TableColumn<VehicleData, String> columnModelCS;

    @FXML
    private TableColumn<VehicleData, Integer> columnYearCS;

    @FXML
    private TableColumn<VehicleData, String> columnOwnerNameCS;

    @FXML
    private TableColumn<VehicleData, String> columnInsuranceStatusCS;

    @FXML
    private TableColumn<VehicleData, String> columnStatusCS;

    @FXML
    private TableColumn<VehicleData, Button> columnActionCS;

    @FXML
    private MenuButton dropdownAllmakes;

    @FXML
    private MenuButton dropdownModelCS;

    @FXML
    private MenuButton dropdownmaxpriceCS;

    @FXML
    private MenuButton dropdownminpriceCS;

    @FXML
    private ImageView imageBackground;

    @FXML
    private Label lblNameCS;

    @FXML
    private Label lblNeedHelpCS;

    @FXML
    private Label lblRoleCustomerCS;

    @FXML
    private Label lblSearch;

    @FXML
    private Label lblWeareheretoassistyou;

    @FXML
    private Label lblWithconfidence;

    @FXML
    private Label lblfind;

    @FXML
    private Label lblperfectcar;

    @FXML
    private Pane paneCustmer;

    @FXML
    private Pane paneCustomer;

    @FXML
    private Pane panefilterCS;

    @FXML
    private Pane root;

    @FXML
    private ScrollBar scrollbar;

    @FXML
    private TableView<VehicleData> tableviewCS;

    @FXML
    private TextField txtSearchCS;

    private String loggedInUser = "";
    private int customerId = 0;
    private ObservableList<VehicleData> vehicleList = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int itemsPerPage = 10;

    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
        lblNameCS.setText(username);
        lblRoleCustomerCS.setText("Customer");
        setupFadeAnimation();
        getCustomerId();
        loadAvailableVehicles();
    }

    private void setupFadeAnimation() {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), btnSearchVehicleCS);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
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

    private void loadAvailableVehicles() {
        vehicleList.clear();
        try (Connection conn = ConnectionDB.getConnection()) {
            String sql = "SELECT v.vehicle_id, v.registration_number, v.make, v.model, v.year, v.price, " +
                    "c.name as owner_name, c.email as owner_email, v.status, " +
                    "CASE WHEN ip.policy_id IS NOT NULL AND ip.end_date >= CURRENT_DATE THEN 'Active' ELSE 'No Insurance' END as insurance_status " +
                    "FROM Vehicle v " +
                    "JOIN Customer c ON v.owner_id = c.customer_id " +
                    "LEFT JOIN InsurancePolicy ip ON v.vehicle_id = ip.vehicle_id AND ip.end_date >= CURRENT_DATE " +
                    "WHERE v.status = 'Available' " +
                    "ORDER BY v.vehicle_id";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                VehicleData vehicle = new VehicleData(
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getString("owner_name"),
                        rs.getString("insurance_status"),
                        rs.getString("status"),
                        rs.getDouble("price"),
                        rs.getString("owner_email")
                );
                vehicleList.add(vehicle);
            }
            rs.close();
            pstmt.close();

            // Set up table columns
            columnIDCS.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
            colunmRegistrationNumberCS.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
            columnMakeCS.setCellValueFactory(new PropertyValueFactory<>("make"));
            columnModelCS.setCellValueFactory(new PropertyValueFactory<>("model"));
            columnYearCS.setCellValueFactory(new PropertyValueFactory<>("year"));
            columnOwnerNameCS.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
            columnInsuranceStatusCS.setCellValueFactory(new PropertyValueFactory<>("insuranceStatus"));
            columnStatusCS.setCellValueFactory(new PropertyValueFactory<>("status"));

            // Apply pagination
            applyPagination();

            // Show total count
            lblSearch.setText("Found " + vehicleList.size() + " vehicles available");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load vehicles: " + e.getMessage());
        }
    }

    private void applyPagination() {
        int fromIndex = currentPage * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, vehicleList.size());

        if (fromIndex < vehicleList.size()) {
            ObservableList<VehicleData> pageList = FXCollections.observableArrayList(vehicleList.subList(fromIndex, toIndex));
            tableviewCS.setItems(pageList);
        } else {
            tableviewCS.setItems(FXCollections.observableArrayList());
        }
    }

    private void nextPage() {
        if ((currentPage + 1) * itemsPerPage < vehicleList.size()) {
            currentPage++;
            applyPagination();
        }
    }

    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            applyPagination();
        }
    }

    private void searchVehicles(String searchText) {
        vehicleList.clear();
        try (Connection conn = ConnectionDB.getConnection()) {
            String sql = "SELECT v.vehicle_id, v.registration_number, v.make, v.model, v.year, v.price, " +
                    "c.name as owner_name, c.email as owner_email, v.status, " +
                    "CASE WHEN ip.policy_id IS NOT NULL AND ip.end_date >= CURRENT_DATE THEN 'Active' ELSE 'No Insurance' END as insurance_status " +
                    "FROM Vehicle v " +
                    "JOIN Customer c ON v.owner_id = c.customer_id " +
                    "LEFT JOIN InsurancePolicy ip ON v.vehicle_id = ip.vehicle_id AND ip.end_date >= CURRENT_DATE " +
                    "WHERE v.status = 'Available' AND (v.make ILIKE ? OR v.model ILIKE ? OR v.registration_number ILIKE ?) " +
                    "ORDER BY v.vehicle_id";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                VehicleData vehicle = new VehicleData(
                        rs.getInt("vehicle_id"),
                        rs.getString("registration_number"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getString("owner_name"),
                        rs.getString("insurance_status"),
                        rs.getString("status"),
                        rs.getDouble("price"),
                        rs.getString("owner_email")
                );
                vehicleList.add(vehicle);
            }
            rs.close();
            pstmt.close();

            currentPage = 0;
            applyPagination();
            lblSearch.setText("Found " + vehicleList.size() + " vehicles matching '" + searchText + "'");

            if (vehicleList.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Results", "No vehicles found matching: " + searchText);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to search vehicles: " + e.getMessage());
        }
    }

    @FXML
    void OnclickBuyCarCS(ActionEvent event) {
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Buy Car page");
        }
    }

    @FXML
    void OnclickseellcarCS(ActionEvent event) {
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Sell Car page");
        }
    }

    @FXML
    void onclicMyvehicleCS(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/MyVehiclePage.fxml"));
            Parent root = loader.load();
            MyVehiclePage myVehiclePage = loader.getController();
            myVehiclePage.setLoggedInUser(loggedInUser);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("My Vehicles");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load My Vehicles page");
        }
    }

    @FXML
    void onclickLogout(ActionEvent event) {
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
    void onclickSearchCS(ActionEvent event) {
        String searchText = txtSearchCS.getText();
        if (!searchText.isEmpty()) {
            searchVehicles(searchText);
        } else {
            currentPage = 0;
            loadAvailableVehicles();
        }
    }

    @FXML
    void onclickcustomerSupport(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Customer Support", "Contact us: support@vehicleid.com\nPhone: +27 123 456 789\nEmail: help@vehicleid.com");
    }

    @FXML
    void onNextPage(ActionEvent event) {
        nextPage();
    }

    @FXML
    void onPreviousPage(ActionEvent event) {
        previousPage();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}