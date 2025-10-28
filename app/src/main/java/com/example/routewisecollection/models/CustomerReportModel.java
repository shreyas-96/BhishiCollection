package com.example.routewisecollection.models;

public class CustomerReportModel {
    private String name;
    private String phoneNumber;
    private String address;
    private int totalEntries;
    private String dateRange;
    private double totalAmount;

    public CustomerReportModel() {
    }

    public CustomerReportModel(String name, String phoneNumber, String address,
                               int totalEntries, String dateRange, double totalAmount) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.totalEntries = totalEntries;
        this.dateRange = dateRange;
        this.totalAmount = totalAmount;
    }

    // Getters and setters
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public int getTotalEntries() { return totalEntries; }
    public String getDateRange() { return dateRange; }
    public double getTotalAmount() { return totalAmount; }

    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setTotalEntries(int totalEntries) { this.totalEntries = totalEntries; }
    public void setDateRange(String dateRange) { this.dateRange = dateRange; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
