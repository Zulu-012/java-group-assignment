package com.example.vehicle_identification_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SellCarPage {

    @FXML
    private MenuItem DropdownYear1;

    @FXML
    private MenuItem DropdownYear2;

    @FXML
    private MenuButton SPDropdownFuelType;

    @FXML
    private Pane SPRequirementsCart;

    @FXML
    private Button SPbtnCustomerSupport;

    @FXML
    private Button SPbtnReset;

    @FXML
    private Button SPbtnSellcar;

    @FXML
    private Button SPbtnSubmit;

    @FXML
    private Button SPbtnbuycar;

    @FXML
    private Button SPbtnlogout;

    @FXML
    private Pane SPcsutomercart;

    @FXML
    private Pane SPcustomerCart;

    @FXML
    private MenuButton SPdropdownYear;

    @FXML
    private Label SPlblDashboard;

    @FXML
    private Label SPlblFueltype;

    @FXML
    private Label SPlblMake;

    @FXML
    private Label SPlblName;

    @FXML
    private Label SPlblREgistrationNumber;

    @FXML
    private Label SPlblVehicleinformation;

    @FXML
    private Label SPlblYear;

    @FXML
    private Label SPlblcustomerrole;

    @FXML
    private Label SPlblheader;

    @FXML
    private Label SPlblmilage;

    @FXML
    private Label SPlblmodel;

    @FXML
    private Label SPlblneedHelp;

    @FXML
    private Label SPlblprice;

    @FXML
    private Label SPlblrequirements;

    @FXML
    private Label SPlblrequirements1;

    @FXML
    private Label SPlblrequirements2;

    @FXML
    private Label SPlblrequirements3;

    @FXML
    private Label SPlblrequirements4;

    @FXML
    private Label SPlblrequirements5;

    @FXML
    private Label SPlblvehicleidentification;

    @FXML
    private Label SPlblweare;

    @FXML
    private Pane SPsellCarCart;

    @FXML
    private Pane SPsidenavigation;

    @FXML
    private TextField SPtxtMake;

    @FXML
    private TextField SPtxtMilage;

    @FXML
    private TextField SPtxtModel;

    @FXML
    private TextField SPtxtPrice;

    @FXML
    private TextField SPtxtREgistrationNumber;

    @FXML
    private TextField SPtxtSearch;

    @FXML
    private Button SpbtnMyvehicle;

    @FXML
    private MenuItem dropdownFuelType1;

    @FXML
    private MenuItem dropdownFuelType2;

    @FXML
    private Pane root;

    @FXML
    private ScrollBar scrollbar;

    @FXML
    private MenuItem year2022;

    @FXML
    private MenuItem year2023;

    @FXML
    private MenuItem year2024;

    @FXML
    private MenuItem fuelTypeElectric;

    @FXML
    private MenuItem fuelTypeHybrid;

    private String loggedInUser = "";
    private int customerId = 0;
    private String selectedYear = "";
    private String selectedFuelType = "";

    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
        SPlblName.setText(username);
        setupDropdowns();
        getCustomerId();
    }

    private void setupDropdowns() {
        // Setup year dropdown items with click handlers
        if (DropdownYear1 != null) {
            DropdownYear1.setText("2020");
            DropdownYear1.setOnAction(e -> selectYear("2020"));
        }
        if (DropdownYear2 != null) {
            DropdownYear2.setText("2021");
            DropdownYear2.setOnAction(e -> selectYear("2021"));
        }
        if (year2022 != null) {
            year2022.setText("2022");
            year2022.setOnAction(e -> selectYear("2022"));
        }
        if (year2023 != null) {
            year2023.setText("2023");
            year2023.setOnAction(e -> selectYear("2023"));
        }
        if (year2024 != null) {
            year2024.setText("2024");
            year2024.setOnAction(e -> selectYear("2024"));
        }

        // Setup fuel type dropdown items with click handlers
        if (dropdownFuelType1 != null) {
            dropdownFuelType1.setText("Petrol");
            dropdownFuelType1.setOnAction(e -> selectFuelType("Petrol"));
        }
        if (dropdownFuelType2 != null) {
            dropdownFuelType2.setText("Diesel");
            dropdownFuelType2.setOnAction(e -> selectFuelType("Diesel"));
        }
        if (fuelTypeElectric != null) {
            fuelTypeElectric.setText("Electric");
            fuelTypeElectric.setOnAction(e -> selectFuelType("Electric"));
        }
        if (fuelTypeHybrid != null) {
            fuelTypeHybrid.setText("Hybrid");
            fuelTypeHybrid.setOnAction(e -> selectFuelType("Hybrid"));
        }
    }

    private void selectYear(String year) {
        selectedYear = year;
        SPdropdownYear.setText(year);
    }

    private void selectFuelType(String fuelType) {
        selectedFuelType = fuelType;
        SPDropdownFuelType.setText(fuelType);
    }

    private void getCustomerId() {
        try (Connection conn = ConnectionDB.getConnection()) {
            // First try to get existing customer
            String sql = "SELECT customer_id FROM Customer WHERE name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, loggedInUser);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                customerId = rs.getInt("customer_id");
            } else {
                // If customer doesn't exist, create one with unique email
                String uniqueEmail = loggedInUser.replaceAll("\\s+", "") + System.currentTimeMillis() + "@vehicleid.com";
                String insertSql = "INSERT INTO Customer (name, phone, email) VALUES (?, ?, ?) RETURNING customer_id";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, loggedInUser);
                insertStmt.setString(2, "0000000000");
                insertStmt.setString(3, uniqueEmail);
                ResultSet insertRs = insertStmt.executeQuery();
                if (insertRs.next()) {
                    customerId = insertRs.getInt("customer_id");
                }
                insertRs.close();
                insertStmt.close();
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to get customer ID: " + e.getMessage());
        }
    }

    @FXML
    void onclickCustomerSP(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Customer Support", "Contact us: support@vehicleid.com\nPhone: +1-555-0123");
    }

    @FXML
    void onclickMyvehicleSP(ActionEvent event) {
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
        }
    }

    @FXML
    void onclickReset(ActionEvent event) {
        SPtxtREgistrationNumber.clear();
        SPtxtMake.clear();
        SPtxtModel.clear();
        SPtxtMilage.clear();
        SPtxtPrice.clear();
        SPdropdownYear.setText("Select Year");
        SPDropdownFuelType.setText("Select Fuel Type");
        selectedYear = "";
        selectedFuelType = "";
    }

    @FXML
    void onclickSellcarSp(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Sell Car", "You are already on Sell Car page");
    }

    @FXML
    void onclickSubmitSP(ActionEvent event) {
        // Check if customerId is valid
        if (customerId == 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please try again. Customer information not found.");
            getCustomerId();
            return;
        }

        String registration = SPtxtREgistrationNumber.getText().toUpperCase();
        String make = SPtxtMake.getText();
        String model = SPtxtModel.getText();
        String yearText = selectedYear;
        String fuelType = selectedFuelType;
        String mileageText = SPtxtMilage.getText();
        String priceText = SPtxtPrice.getText();

        // Validation - Check if dropdowns have selections
        if (yearText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a year from the dropdown!");
            return;
        }

        if (fuelType.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a fuel type from the dropdown!");
            return;
        }

        if (registration.isEmpty() || make.isEmpty() || model.isEmpty() ||
                mileageText.isEmpty() || priceText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all text fields!");
            return;
        }

        // Validate year
        int year;
        try {
            year = Integer.parseInt(yearText);
            if (year < 1900 || year > 2026) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid year (1900-2026)");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a valid year");
            return;
        }

        // Validate mileage
        int mileage;
        try {
            mileage = Integer.parseInt(mileageText);
            if (mileage < 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Mileage cannot be negative");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid mileage number");
            return;
        }

        // Validate price
        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Price cannot be negative");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid price");
            return;
        }

        try (Connection conn = ConnectionDB.getConnection()) {
            // Check if registration number already exists
            String checkSql = "SELECT COUNT(*) FROM Vehicle WHERE registration_number = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, registration);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Vehicle with this registration number already exists!");
                rs.close();
                checkStmt.close();
                return;
            }
            rs.close();
            checkStmt.close();

            // Insert new vehicle
            String sql = "INSERT INTO Vehicle (registration_number, make, model, year, owner_id, fuel_type, mileage, price, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Available')";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, registration);
            pstmt.setString(2, make);
            pstmt.setString(3, model);
            pstmt.setInt(4, year);
            pstmt.setInt(5, customerId);
            pstmt.setString(6, fuelType);
            pstmt.setInt(7, mileage);
            pstmt.setDouble(8, price);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Your vehicle has been listed for sale successfully!");
                onclickReset(event);
            }
            pstmt.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to list vehicle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onclickbuycarSP(ActionEvent event) {
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
    void onclicklogoutSP(ActionEvent event) {
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}