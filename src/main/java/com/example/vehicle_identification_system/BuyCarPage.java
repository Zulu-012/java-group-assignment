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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class BuyCarPage {

    @FXML
    private Button btnBuyCar;

    @FXML
    private Button btnBuyCar2;

    @FXML
    private Button btnCustomerSupport;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnMyVehicle;

    @FXML
    private Button btnSearchVehicle;

    @FXML
    private Button btnSellCar;

    @FXML
    private TableColumn<VehicleData, Integer> colunmid;

    @FXML
    private TableColumn<VehicleData, String> columnRegistrationNumber;

    @FXML
    private TableColumn<VehicleData, String> columnMake;

    @FXML
    private TableColumn<VehicleData, String> columnModel;

    @FXML
    private TableColumn<VehicleData, Integer> columnyear;

    @FXML
    private TableColumn<VehicleData, String> columnOwnername;

    @FXML
    private TableColumn<VehicleData, String> columnInsuranceStatus;

    @FXML
    private TableColumn<VehicleData, String> columnStatus;

    @FXML
    private MenuItem dropDownmake2;

    @FXML
    private MenuButton dropdownAllMakes;

    @FXML
    private MenuButton dropdownAllmodel;

    @FXML
    private MenuItem dropdownMake1;

    @FXML
    private MenuButton dropdownMinprice;

    @FXML
    private MenuItem dropdownMoodel2;

    @FXML
    private MenuButton dropdownmaxprice;

    @FXML
    private MenuItem dropdownmaxprice1;

    @FXML
    private MenuItem dropdownmaxprice2;

    @FXML
    private MenuItem dropdownminprice1;

    @FXML
    private MenuItem dropdownminprice2;

    @FXML
    private MenuItem dropdownmodel1;

    @FXML
    private Label lblHeader;

    @FXML
    private Label lblNeedHelp;

    @FXML
    private Label lblSytem;

    @FXML
    private Label lblVehicleidetification;

    @FXML
    private Label lblWeareheretoassistyou;

    @FXML
    private Label lblroleCustomer;

    @FXML
    private Label lbluserName;

    @FXML
    private Pane paneCustomer;

    @FXML
    private Pane paneFilter;

    @FXML
    private Pane paneUser;

    @FXML
    private Pane root;

    @FXML
    private ScrollBar scrollbar;

    @FXML
    private Pane sideNavigation;

    @FXML
    private TableView<VehicleData> tableview;

    @FXML
    private TextField txtSearch;

    private String loggedInUser = "";
    private String loggedInUserEmail = "";
    private ObservableList<VehicleData> vehicleList = FXCollections.observableArrayList();
    private ObservableList<VehicleData> filteredList = FXCollections.observableArrayList();
    private String selectedMake = "";
    private String selectedModel = "";
    private Double minPrice = null;
    private Double maxPrice = null;

    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
        lbluserName.setText(username);
        getUserEmail();
        setupFilters();
        loadAllVehicles();
    }

    private void getUserEmail() {
        try (Connection conn = ConnectionDB.getConnection()) {
            String sql = "SELECT email FROM Users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, loggedInUser);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                loggedInUserEmail = rs.getString("email");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupFilters() {
        dropdownMake1.setText("Toyota");
        dropDownmake2.setText("Honda");
        dropdownMake1.setOnAction(e -> selectMake("Toyota"));
        dropDownmake2.setOnAction(e -> selectMake("Honda"));

        loadMakesFromDatabase();

        dropdownmodel1.setText("Camry");
        dropdownMoodel2.setText("Civic");
        dropdownmodel1.setOnAction(e -> selectModel("Camry"));
        dropdownMoodel2.setOnAction(e -> selectModel("Civic"));

        dropdownminprice1.setText("R0");
        dropdownminprice2.setText("R50,000");
        dropdownminprice1.setOnAction(e -> selectMinPrice(0));
        dropdownminprice2.setOnAction(e -> selectMinPrice(50000));

        dropdownmaxprice1.setText("R100,000");
        dropdownmaxprice2.setText("R500,000");
        dropdownmaxprice1.setOnAction(e -> selectMaxPrice(100000));
        dropdownmaxprice2.setOnAction(e -> selectMaxPrice(500000));
    }

    private void loadMakesFromDatabase() {
        try (Connection conn = ConnectionDB.getConnection()) {
            String sql = "SELECT DISTINCT make FROM Vehicle WHERE status = 'Available' ORDER BY make";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            dropdownAllMakes.getItems().clear();
            MenuItem allMakes = new MenuItem("All Makes");
            allMakes.setOnAction(e -> {
                selectedMake = "";
                dropdownAllMakes.setText("All Makes");
                applyFilters();
            });
            dropdownAllMakes.getItems().add(allMakes);

            while (rs.next()) {
                String make = rs.getString("make");
                MenuItem makeItem = new MenuItem(make);
                makeItem.setOnAction(e -> selectMake(make));
                dropdownAllMakes.getItems().add(makeItem);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectMake(String make) {
        this.selectedMake = make;
        dropdownAllMakes.setText(make);
        applyFilters();
        loadModelsForMake(make);
    }

    private void loadModelsForMake(String make) {
        try (Connection conn = ConnectionDB.getConnection()) {
            String sql = "SELECT DISTINCT model FROM Vehicle WHERE make = ? AND status = 'Available' ORDER BY model";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, make);
            ResultSet rs = pstmt.executeQuery();

            dropdownAllmodel.getItems().clear();
            MenuItem allModels = new MenuItem("All Models");
            allModels.setOnAction(e -> {
                selectedModel = "";
                dropdownAllmodel.setText("All Models");
                applyFilters();
            });
            dropdownAllmodel.getItems().add(allModels);

            while (rs.next()) {
                String model = rs.getString("model");
                MenuItem modelItem = new MenuItem(model);
                modelItem.setOnAction(e -> selectModel(model));
                dropdownAllmodel.getItems().add(modelItem);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectModel(String model) {
        this.selectedModel = model;
        dropdownAllmodel.setText(model);
        applyFilters();
    }

    private void selectMinPrice(double price) {
        this.minPrice = price;
        dropdownMinprice.setText("Min: R" + String.format("%,.0f", price));
        applyFilters();
    }

    private void selectMaxPrice(double price) {
        this.maxPrice = price;
        dropdownmaxprice.setText("Max: R" + String.format("%,.0f", price));
        applyFilters();
    }

    private void loadAllVehicles() {
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

            colunmid.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
            columnRegistrationNumber.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
            columnMake.setCellValueFactory(new PropertyValueFactory<>("make"));
            columnModel.setCellValueFactory(new PropertyValueFactory<>("model"));
            columnyear.setCellValueFactory(new PropertyValueFactory<>("year"));
            columnOwnername.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
            columnInsuranceStatus.setCellValueFactory(new PropertyValueFactory<>("insuranceStatus"));
            columnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            filteredList.setAll(vehicleList);
            tableview.setItems(filteredList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load vehicles: " + e.getMessage());
        }
    }

    private void applyFilters() {
        filteredList.clear();
        for (VehicleData vehicle : vehicleList) {
            boolean matchesMake = selectedMake.isEmpty() || vehicle.getMake().equalsIgnoreCase(selectedMake);
            boolean matchesModel = selectedModel.isEmpty() || vehicle.getModel().equalsIgnoreCase(selectedModel);
            boolean matchesMinPrice = minPrice == null || vehicle.getPrice() >= minPrice;
            boolean matchesMaxPrice = maxPrice == null || vehicle.getPrice() <= maxPrice;
            if (matchesMake && matchesModel && matchesMinPrice && matchesMaxPrice) {
                filteredList.add(vehicle);
            }
        }
        tableview.setItems(filteredList);
        if (filteredList.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Results", "No vehicles match your filters");
        }
    }

    private void searchVehicles(String searchText) {
        filteredList.clear();
        String searchLower = searchText.toLowerCase();
        for (VehicleData vehicle : vehicleList) {
            if (vehicle.getMake().toLowerCase().contains(searchLower) ||
                    vehicle.getModel().toLowerCase().contains(searchLower) ||
                    vehicle.getRegistrationNumber().toLowerCase().contains(searchLower)) {
                filteredList.add(vehicle);
            }
        }
        tableview.setItems(filteredList);
        if (filteredList.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Results", "No vehicles match your search: " + searchText);
        }
    }

    @FXML
    void OnclickBuyCar(ActionEvent event) {
        VehicleData selectedVehicle = tableview.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a vehicle to purchase");
            return;
        }
        showEmailDialog(selectedVehicle);
    }

    private void showEmailDialog(VehicleData vehicle) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Contact Seller");
        dialog.setHeaderText("Contact seller about: " + vehicle.getMake() + " " + vehicle.getModel());

        ButtonType sendButtonType = new ButtonType("Send Email", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));

        TextField toField = new TextField(vehicle.getOwnerEmail());
        toField.setEditable(false);
        toField.setPrefWidth(400);

        TextField subjectField = new TextField();
        subjectField.setText("Interest in your vehicle: " + vehicle.getMake() + " " + vehicle.getModel());

        TextArea messageArea = new TextArea();
        messageArea.setPrefRowCount(5);
        messageArea.setPrefWidth(400);
        messageArea.setPromptText("Write your message here...");

        grid.add(new Label("To:"), 0, 0);
        grid.add(toField, 1, 0);
        grid.add(new Label("Subject:"), 0, 1);
        grid.add(subjectField, 1, 1);
        grid.add(new Label("Message:"), 0, 2);
        grid.add(messageArea, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == sendButtonType) {
                return subjectField.getText() + "|" + messageArea.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(data -> {
            String[] parts = data.split("\\|", 2);
            String subject = parts[0];
            String message = parts[1];
            sendEmail(vehicle.getOwnerEmail(), subject, message, vehicle);
        });
    }

    private void sendEmail(String toEmail, String subject, String message, VehicleData vehicle) {
        try {
            String emailBody = message + "\n\n--- Vehicle Details ---\n" +
                    "Registration: " + vehicle.getRegistrationNumber() + "\n" +
                    "Make: " + vehicle.getMake() + "\n" +
                    "Model: " + vehicle.getModel() + "\n" +
                    "Year: " + vehicle.getYear() + "\n" +
                    "Price: R" + String.format("%,.2f", vehicle.getPrice()) + "\n\n" +
                    "From: " + loggedInUser + " (" + loggedInUserEmail + ")";

            String encodedSubject = java.net.URLEncoder.encode(subject, "UTF-8");
            String encodedBody = java.net.URLEncoder.encode(emailBody, "UTF-8");
            String mailtoUrl = "mailto:" + toEmail + "?subject=" + encodedSubject + "&body=" + encodedBody;
            java.awt.Desktop.getDesktop().mail(new java.net.URI(mailtoUrl));
            showAlert(Alert.AlertType.INFORMATION, "Email Client", "Your email client has been opened.");
            saveInquiryToDatabase(vehicle, message);
        } catch (Exception e) {
            showAlert(Alert.AlertType.INFORMATION, "Contact Seller",
                    "Please contact the seller directly at:\n\nSeller Email: " + toEmail);
        }
    }

    private void saveInquiryToDatabase(VehicleData vehicle, String message) {
        try (Connection conn = ConnectionDB.getConnection()) {
            String sql = "INSERT INTO CustomerQuery (customer_id, vehicle_id, query_date, query_text, status) " +
                    "VALUES ((SELECT customer_id FROM Customer WHERE name = ?), ?, CURRENT_DATE, ?, 'Pending')";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, loggedInUser);
            pstmt.setInt(2, vehicle.getVehicleId());
            pstmt.setString(3, "Interested in buying: " + vehicle.getMake() + " " + vehicle.getModel() +
                    "\nRegistration: " + vehicle.getRegistrationNumber() +
                    "\nMessage: " + message);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void OnclickLogout(ActionEvent event) {
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
    void OnclickMainDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_identification_system/CustomerPage.fxml"));
            Parent root = loader.load();
            CustomerPage customerPage = loader.getController();
            customerPage.setLoggedInUser(loggedInUser);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Customer Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Customer Dashboard");
        }
    }

    @FXML
    void OnclickMyVehicle(ActionEvent event) {
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
    void OnclickSellCar(ActionEvent event) {
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

    @FXML
    void OnclickSupport(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Customer Support",
                "Contact us: support@vehicleid.com\nPhone: +27 123 456 789");
    }

    @FXML
    void onClickSearch(ActionEvent event) {
        String searchText = txtSearch.getText();
        if (!searchText.isEmpty()) {
            searchVehicles(searchText);
        } else {
            applyFilters();
        }
    }

    @FXML
    void onclickpurchasecar(ActionEvent event) {
        OnclickBuyCar(event);
    }

    @FXML
    void onClearFilters(ActionEvent event) {
        selectedMake = "";
        selectedModel = "";
        minPrice = null;
        maxPrice = null;
        dropdownAllMakes.setText("All Makes");
        dropdownAllmodel.setText("All Models");
        dropdownMinprice.setText("Min Price");
        dropdownmaxprice.setText("Max Price");
        txtSearch.clear();
        applyFilters();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}