package com.example.vehicle_identification_system;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.LocalDate;

public class ViolationData {
    private final SimpleIntegerProperty violationId;
    private final SimpleStringProperty registrationNumber;
    private final SimpleStringProperty make;
    private final SimpleStringProperty model;
    private final SimpleStringProperty violationType;
    private final SimpleObjectProperty<LocalDate> violationDate;
    private final SimpleDoubleProperty fineAmount;
    private final SimpleStringProperty status;
    private final SimpleStringProperty location;
    private final SimpleStringProperty officerName;

    public ViolationData(int violationId, String registrationNumber, String make, String model,
                         String violationType, LocalDate violationDate, double fineAmount,
                         String status, String location, String officerName) {
        this.violationId = new SimpleIntegerProperty(violationId);
        this.registrationNumber = new SimpleStringProperty(registrationNumber);
        this.make = new SimpleStringProperty(make);
        this.model = new SimpleStringProperty(model);
        this.violationType = new SimpleStringProperty(violationType);
        this.violationDate = new SimpleObjectProperty<>(violationDate);
        this.fineAmount = new SimpleDoubleProperty(fineAmount);
        this.status = new SimpleStringProperty(status);
        this.location = new SimpleStringProperty(location);
        this.officerName = new SimpleStringProperty(officerName);
    }

    // Getters
    public int getViolationId() { return violationId.get(); }
    public String getRegistrationNumber() { return registrationNumber.get(); }
    public String getMake() { return make.get(); }
    public String getModel() { return model.get(); }
    public String getViolationType() { return violationType.get(); }
    public LocalDate getViolationDate() { return violationDate.get(); }
    public double getFineAmount() { return fineAmount.get(); }
    public String getStatus() { return status.get(); }
    public String getLocation() { return location.get(); }
    public String getOfficerName() { return officerName.get(); }

    // Property getters
    public SimpleIntegerProperty violationIdProperty() { return violationId; }
    public SimpleStringProperty registrationNumberProperty() { return registrationNumber; }
    public SimpleStringProperty makeProperty() { return make; }
    public SimpleStringProperty modelProperty() { return model; }
    public SimpleStringProperty violationTypeProperty() { return violationType; }
    public SimpleObjectProperty<LocalDate> violationDateProperty() { return violationDate; }
    public SimpleDoubleProperty fineAmountProperty() { return fineAmount; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty locationProperty() { return location; }
    public SimpleStringProperty officerNameProperty() { return officerName; }
}