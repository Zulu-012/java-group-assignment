package com.example.vehicle_identification_system;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class VehicleData {
    private final SimpleIntegerProperty vehicleId;
    private final SimpleStringProperty registrationNumber;
    private final SimpleStringProperty make;
    private final SimpleStringProperty model;
    private final SimpleIntegerProperty year;
    private final SimpleStringProperty ownerName;
    private final SimpleStringProperty insuranceStatus;
    private final SimpleStringProperty status;
    private final SimpleStringProperty fuelType;
    private final SimpleIntegerProperty mileage;
    private final SimpleDoubleProperty price;
    private final SimpleStringProperty ownerEmail;

    // Constructor for BuyCarPage and CustomerPage (10 parameters - with price and email)
    public VehicleData(int vehicleId, String registrationNumber, String make, String model,
                       int year, String ownerName, String insuranceStatus, String status,
                       double price, String ownerEmail) {
        this.vehicleId = new SimpleIntegerProperty(vehicleId);
        this.registrationNumber = new SimpleStringProperty(registrationNumber);
        this.make = new SimpleStringProperty(make);
        this.model = new SimpleStringProperty(model);
        this.year = new SimpleIntegerProperty(year);
        this.ownerName = new SimpleStringProperty(ownerName);
        this.insuranceStatus = new SimpleStringProperty(insuranceStatus);
        this.status = new SimpleStringProperty(status);
        this.fuelType = new SimpleStringProperty("");
        this.mileage = new SimpleIntegerProperty(0);
        this.price = new SimpleDoubleProperty(price);
        this.ownerEmail = new SimpleStringProperty(ownerEmail);
    }

    // Constructor for MyVehiclePage (10 parameters - with fuelType, mileage, price)
    public VehicleData(int vehicleId, String registrationNumber, String make, String model,
                       int year, String fuelType, int mileage, double price, String insuranceStatus, String status) {
        this.vehicleId = new SimpleIntegerProperty(vehicleId);
        this.registrationNumber = new SimpleStringProperty(registrationNumber);
        this.make = new SimpleStringProperty(make);
        this.model = new SimpleStringProperty(model);
        this.year = new SimpleIntegerProperty(year);
        this.ownerName = new SimpleStringProperty("");
        this.insuranceStatus = new SimpleStringProperty(insuranceStatus);
        this.status = new SimpleStringProperty(status);
        this.fuelType = new SimpleStringProperty(fuelType);
        this.mileage = new SimpleIntegerProperty(mileage);
        this.price = new SimpleDoubleProperty(price);
        this.ownerEmail = new SimpleStringProperty("");
    }

    // Getters
    public int getVehicleId() { return vehicleId.get(); }
    public String getRegistrationNumber() { return registrationNumber.get(); }
    public String getMake() { return make.get(); }
    public String getModel() { return model.get(); }
    public int getYear() { return year.get(); }
    public String getOwnerName() { return ownerName.get(); }
    public String getInsuranceStatus() { return insuranceStatus.get(); }
    public String getStatus() { return status.get(); }
    public String getFuelType() { return fuelType.get(); }
    public int getMileage() { return mileage.get(); }
    public double getPrice() { return price.get(); }
    public String getOwnerEmail() { return ownerEmail.get(); }

    // Property getters for TableView
    public SimpleIntegerProperty vehicleIdProperty() { return vehicleId; }
    public SimpleStringProperty registrationNumberProperty() { return registrationNumber; }
    public SimpleStringProperty makeProperty() { return make; }
    public SimpleStringProperty modelProperty() { return model; }
    public SimpleIntegerProperty yearProperty() { return year; }
    public SimpleStringProperty ownerNameProperty() { return ownerName; }
    public SimpleStringProperty insuranceStatusProperty() { return insuranceStatus; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty fuelTypeProperty() { return fuelType; }
    public SimpleIntegerProperty mileageProperty() { return mileage; }
    public SimpleDoubleProperty priceProperty() { return price; }
    public SimpleStringProperty ownerEmailProperty() { return ownerEmail; }
}