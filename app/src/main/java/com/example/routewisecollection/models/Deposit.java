package com.example.routewisecollection.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "deposits")
public class Deposit {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "customerId")
    private int customerId;

    @ColumnInfo(name = "accountNumber")  // Add this line for new field
    private String accountNumber;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "depositDate")
    private String depositDate;

    @ColumnInfo(name = "depositTime")
    private String depositTime;

    @ColumnInfo(name = "interestAmount")
    private double interestAmount;

    @ColumnInfo(name = "totalAmount")
    private double totalAmount;

    @ColumnInfo(name = "paymentMethod")
    private String paymentMethod;

    @ColumnInfo(name = "receiptNumber")
    private String receiptNumber;

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "customerName")
    private String customerName;

    // Default constructor for Room
    public Deposit() {}

    // Full constructor (update as needed to include accountNumber)
    public Deposit(int customerId, String accountNumber, double amount, String depositDate,
                   String depositTime, double interestAmount, double totalAmount,
                   String paymentMethod, String receiptNumber, String notes, String customerName) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.depositDate = depositDate;
        this.depositTime = depositTime;
        this.interestAmount = interestAmount;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
        this.notes = notes;
        this.customerName = customerName;
    }

    // Getters and Setters for all fields including accountNumber
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDepositDate() {
        return depositDate;
    }
    public void setDepositDate(String depositDate) {
        this.depositDate = depositDate;
    }

    public String getDepositTime() {
        return depositTime;
    }
    public void setDepositTime(String depositTime) {
        this.depositTime = depositTime;
    }

    public double getInterestAmount() {
        return interestAmount;
    }
    public void setInterestAmount(double interestAmount) {
        this.interestAmount = interestAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }
    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
