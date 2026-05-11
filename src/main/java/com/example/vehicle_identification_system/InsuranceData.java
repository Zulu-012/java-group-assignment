package com.example.vehicle_identification_system;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.LocalDate;

public class InsuranceData {
    private final SimpleIntegerProperty policyId;
    private final SimpleStringProperty policyNumber;
    private final SimpleStringProperty vehicleNumber;
    private final SimpleStringProperty customerName;
    private final SimpleObjectProperty<LocalDate> startDate;
    private final SimpleObjectProperty<LocalDate> endDate;
    private final SimpleDoubleProperty claimAmount;
    private final SimpleStringProperty status;
    private final SimpleStringProperty insuranceCompany;

    // Constructor for policies and claims
    public InsuranceData(int policyId, String policyNumber, String vehicleNumber, String customerName,
                         LocalDate startDate, LocalDate endDate, double claimAmount, String status, String insuranceCompany) {
        this.policyId = new SimpleIntegerProperty(policyId);
        this.policyNumber = new SimpleStringProperty(policyNumber);
        this.vehicleNumber = new SimpleStringProperty(vehicleNumber);
        this.customerName = new SimpleStringProperty(customerName);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.claimAmount = new SimpleDoubleProperty(claimAmount);
        this.status = new SimpleStringProperty(status);
        this.insuranceCompany = new SimpleStringProperty(insuranceCompany);
    }

    // Getters
    public int getPolicyId() { return policyId.get(); }
    public String getPolicyNumber() { return policyNumber.get(); }
    public String getVehicleNumber() { return vehicleNumber.get(); }
    public String getCustomerName() { return customerName.get(); }
    public LocalDate getStartDate() { return startDate.get(); }
    public LocalDate getEndDate() { return endDate.get(); }
    public double getClaimAmount() { return claimAmount.get(); }
    public String getStatus() { return status.get(); }
    public String getInsuranceCompany() { return insuranceCompany.get(); }

    // Property getters for TableView
    public SimpleIntegerProperty policyIdProperty() { return policyId; }
    public SimpleStringProperty policyNumberProperty() { return policyNumber; }
    public SimpleStringProperty vehicleNumberProperty() { return vehicleNumber; }
    public SimpleStringProperty customerNameProperty() { return customerName; }
    public SimpleObjectProperty<LocalDate> startDateProperty() { return startDate; }
    public SimpleObjectProperty<LocalDate> endDateProperty() { return endDate; }
    public SimpleDoubleProperty claimAmountProperty() { return claimAmount; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty insuranceCompanyProperty() { return insuranceCompany; }
}