package com.example.vehicle_identification_system;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.LocalDate;

public class PoliceReportData {
    private final SimpleIntegerProperty reportId;
    private final SimpleStringProperty registrationNumber;
    private final SimpleStringProperty reportType;
    private final SimpleObjectProperty<LocalDate> reportDate;
    private final SimpleStringProperty location;
    private final SimpleStringProperty officerName;
    private final SimpleStringProperty status;
    private final SimpleStringProperty description;
    // Additional properties for vehicle details in PolicePage
    private final SimpleStringProperty make;
    private final SimpleStringProperty model;
    private final SimpleIntegerProperty year;
    private final SimpleStringProperty ownerName;

    // Constructor for PolicePage (with all vehicle details)
    public PoliceReportData(int reportId, String registrationNumber, String reportType,
                            LocalDate reportDate, String location, String officerName,
                            String status, String description, String make, String model,
                            int year, String ownerName) {
        this.reportId = new SimpleIntegerProperty(reportId);
        this.registrationNumber = new SimpleStringProperty(registrationNumber);
        this.reportType = new SimpleStringProperty(reportType);
        this.reportDate = new SimpleObjectProperty<>(reportDate);
        this.location = new SimpleStringProperty(location);
        this.officerName = new SimpleStringProperty(officerName);
        this.status = new SimpleStringProperty(status);
        this.description = new SimpleStringProperty(description);
        this.make = new SimpleStringProperty(make);
        this.model = new SimpleStringProperty(model);
        this.year = new SimpleIntegerProperty(year);
        this.ownerName = new SimpleStringProperty(ownerName);
    }

    // Constructor for ReportPage (without vehicle details - keeps original working)
    public PoliceReportData(int reportId, String registrationNumber, String reportType,
                            LocalDate reportDate, String location, String officerName,
                            String status, String description) {
        this.reportId = new SimpleIntegerProperty(reportId);
        this.registrationNumber = new SimpleStringProperty(registrationNumber);
        this.reportType = new SimpleStringProperty(reportType);
        this.reportDate = new SimpleObjectProperty<>(reportDate);
        this.location = new SimpleStringProperty(location);
        this.officerName = new SimpleStringProperty(officerName);
        this.status = new SimpleStringProperty(status);
        this.description = new SimpleStringProperty(description);
        this.make = new SimpleStringProperty("N/A");
        this.model = new SimpleStringProperty("N/A");
        this.year = new SimpleIntegerProperty(0);
        this.ownerName = new SimpleStringProperty("N/A");
    }

    // Getters
    public int getReportId() { return reportId.get(); }
    public String getRegistrationNumber() { return registrationNumber.get(); }
    public String getReportType() { return reportType.get(); }
    public LocalDate getReportDate() { return reportDate.get(); }
    public String getLocation() { return location.get(); }
    public String getOfficerName() { return officerName.get(); }
    public String getStatus() { return status.get(); }
    public String getDescription() { return description.get(); }
    public String getMake() { return make.get(); }
    public String getModel() { return model.get(); }
    public int getYear() { return year.get(); }
    public String getOwnerName() { return ownerName.get(); }

    // Property getters for TableView
    public SimpleIntegerProperty reportIdProperty() { return reportId; }
    public SimpleStringProperty registrationNumberProperty() { return registrationNumber; }
    public SimpleStringProperty reportTypeProperty() { return reportType; }
    public SimpleObjectProperty<LocalDate> reportDateProperty() { return reportDate; }
    public SimpleStringProperty locationProperty() { return location; }
    public SimpleStringProperty officerNameProperty() { return officerName; }
    public SimpleStringProperty statusProperty() { return status; }
    public SimpleStringProperty descriptionProperty() { return description; }
    public SimpleStringProperty makeProperty() { return make; }
    public SimpleStringProperty modelProperty() { return model; }
    public SimpleIntegerProperty yearProperty() { return year; }
    public SimpleStringProperty ownerNameProperty() { return ownerName; }
}