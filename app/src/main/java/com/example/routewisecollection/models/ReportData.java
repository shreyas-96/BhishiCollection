package com.example.routewisecollection.models;

import java.util.List;

public class ReportData {
    
    public static class DailyReport {
        private String date;
        private double totalCollection;
        private int totalEntries;
        private List<Deposit> deposits;
        
        public DailyReport(String date, double totalCollection, int totalEntries, List<Deposit> deposits) {
            this.date = date;
            this.totalCollection = totalCollection;
            this.totalEntries = totalEntries;
            this.deposits = deposits;
        }

        // Getters and setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public double getTotalCollection() { return totalCollection; }
        public void setTotalCollection(double totalCollection) { this.totalCollection = totalCollection; }
        
        public int getTotalEntries() { return totalEntries; }
        public void setTotalEntries(int totalEntries) { this.totalEntries = totalEntries; }
        
        public List<Deposit> getDeposits() { return deposits; }
        public void setDeposits(List<Deposit> deposits) { this.deposits = deposits; }
    }
    
    public static class MonthlyReport {
        private String month;
        private String year;
        private double totalCollection;
        private int totalEntries;
        private int totalCustomers;
        private List<DailySummary> dailySummaries;
        
        public MonthlyReport(String month, String year, double totalCollection, 
                           int totalEntries, int totalCustomers, List<DailySummary> dailySummaries) {
            this.month = month;
            this.year = year;
            this.totalCollection = totalCollection;
            this.totalEntries = totalEntries;
            this.totalCustomers = totalCustomers;
            this.dailySummaries = dailySummaries;
        }
        
        // Getters and setters
        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        
        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }
        
        public double getTotalCollection() { return totalCollection; }
        public void setTotalCollection(double totalCollection) { this.totalCollection = totalCollection; }
        
        public int getTotalEntries() { return totalEntries; }
        public void setTotalEntries(int totalEntries) { this.totalEntries = totalEntries; }
        
        public int getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }
        
        public List<DailySummary> getDailySummaries() { return dailySummaries; }
        public void setDailySummaries(List<DailySummary> dailySummaries) { this.dailySummaries = dailySummaries; }
    }
    
    public static class CustomerReport {
        private Customer customer;
        private double totalAmount;
        private int totalEntries;
        private String firstEntryDate;
        private String lastEntryDate;
        private List<Deposit> deposits;
        
        public CustomerReport(Customer customer, double totalAmount, int totalEntries,
                            String firstEntryDate, String lastEntryDate, List<Deposit> deposits) {
            this.customer = customer;
            this.totalAmount = totalAmount;
            this.totalEntries = totalEntries;
            this.firstEntryDate = firstEntryDate;
            this.lastEntryDate = lastEntryDate;
            this.deposits = deposits;
        }
        
        // Getters and setters
        public Customer getCustomer() { return customer; }
        public void setCustomer(Customer customer) { this.customer = customer; }
        
        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
        
        public int getTotalEntries() { return totalEntries; }
        public void setTotalEntries(int totalEntries) { this.totalEntries = totalEntries; }
        
        public String getFirstEntryDate() { return firstEntryDate; }
        public void setFirstEntryDate(String firstEntryDate) { this.firstEntryDate = firstEntryDate; }
        
        public String getLastEntryDate() { return lastEntryDate; }
        public void setLastEntryDate(String lastEntryDate) { this.lastEntryDate = lastEntryDate; }
        
        public List<Deposit> getDeposits() { return deposits; }
        public void setDeposits(List<Deposit> deposits) { this.deposits = deposits; }
    }
    
    public static class WeeklyReport {
        private String weekPeriod;
        private double totalCollection;
        private int totalEntries;
        private List<DailySummary> dailySummaries;
        
        public WeeklyReport(String weekPeriod, double totalCollection, int totalEntries, List<DailySummary> dailySummaries) {
            this.weekPeriod = weekPeriod;
            this.totalCollection = totalCollection;
            this.totalEntries = totalEntries;
            this.dailySummaries = dailySummaries;
        }
        
        // Getters and setters
        public String getWeekPeriod() { return weekPeriod; }
        public void setWeekPeriod(String weekPeriod) { this.weekPeriod = weekPeriod; }
        
        public double getTotalCollection() { return totalCollection; }
        public void setTotalCollection(double totalCollection) { this.totalCollection = totalCollection; }
        
        public int getTotalEntries() { return totalEntries; }
        public void setTotalEntries(int totalEntries) { this.totalEntries = totalEntries; }
        
        public List<DailySummary> getDailySummaries() { return dailySummaries; }
        public void setDailySummaries(List<DailySummary> dailySummaries) { this.dailySummaries = dailySummaries; }
    }
    
    public static class DailySummary {
        private String date;
        private double totalAmount;
        private int entryCount;
        
        public DailySummary(String date, double totalAmount, int entryCount) {
            this.date = date;
            this.totalAmount = totalAmount;
            this.entryCount = entryCount;
        }
        
        // Getters and setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
        
        public int getEntryCount() { return entryCount; }
        public void setEntryCount(int entryCount) { this.entryCount = entryCount; }
    }
}
