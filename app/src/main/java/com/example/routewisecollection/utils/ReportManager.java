package com.example.routewisecollection.utils;

import android.content.Context;
import android.os.Environment;

import com.example.routewisecollection.models.Customer;
import com.example.routewisecollection.models.Deposit;
import com.example.routewisecollection.models.ReportData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportManager {
    
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    
    public ReportManager(Context context) {
        this.context = context;
    }
    
    /**
     * Generate Daily Report
     */
    public ReportData.DailyReport generateDailyReport(List<Deposit> allDeposits, String date) {
        List<Deposit> dailyDeposits = new ArrayList<>();
        double totalCollection = 0.0;
        
        for (Deposit deposit : allDeposits) {
            if (deposit.getDepositDate().equals(date)) {
                dailyDeposits.add(deposit);
                totalCollection += deposit.getAmount();
            }
        }
        
        return new ReportData.DailyReport(date, totalCollection, dailyDeposits.size(), dailyDeposits);
    }
    
    /**
     * Generate Weekly Report
     */
    public ReportData.WeeklyReport generateWeeklyReport(List<Deposit> allTransactions, String startDate, String endDate) {
        Map<String, List<Deposit>> dailyTransactions = new HashMap<>();
        double totalCollection = 0.0;
        int totalEntries = 0;
        
        // Group transactions by date
        for (Deposit transaction : allTransactions) {
            String txDate = transaction.getDepositDate();
            if (txDate != null && txDate.compareTo(startDate) >= 0 && txDate.compareTo(endDate) <= 0) {
                if (!dailyTransactions.containsKey(txDate)) {
                    dailyTransactions.put(txDate, new ArrayList<>());
                }
                dailyTransactions.get(txDate).add(transaction);
                totalCollection += transaction.getAmount();
                totalEntries++;
            }
        }
        
        // Create daily summaries
        List<ReportData.DailySummary> dailySummaries = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateFormat.parse(startDate));
            for (int i = 0; i < 7; i++) {
                String currentDate = dateFormat.format(cal.getTime());
                List<Deposit> dayTransactions = dailyTransactions.get(currentDate);
                double dayTotal = 0.0;
                int dayCount = 0;
                
                if (dayTransactions != null) {
                    for (Deposit tx : dayTransactions) {
                        dayTotal += tx.getAmount();
                        dayCount++;
                    }
                }
                
                dailySummaries.add(new ReportData.DailySummary(currentDate, dayTotal, dayCount));
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String weekPeriod = formatDateRange(startDate, endDate);
        return new ReportData.WeeklyReport(weekPeriod, totalCollection, totalEntries, dailySummaries);
    }
    
    private String formatDateRange(String startDate, String endDate) {
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            return displayDateFormat.format(start) + " - " + displayDateFormat.format(end);
        } catch (Exception e) {
            return startDate + " - " + endDate;
        }
    }
    
    /**
     * Generate Monthly Report
     */
    public ReportData.MonthlyReport generateMonthlyReport(List<Deposit> allDeposits, 
                                                         List<Customer> allCustomers, 
                                                         int month, int year) {
        String monthStr = String.format("%02d", month);
        String yearStr = String.valueOf(year);
        String monthPrefix = yearStr + "-" + monthStr;
        
        List<Deposit> monthlyDeposits = new ArrayList<>();
        Map<String, ReportData.DailySummary> dailySummaryMap = new HashMap<>();
        Map<Integer, Boolean> uniqueCustomers = new HashMap<>(); // Track unique customers
        double totalCollection = 0.0;
        
        for (Deposit deposit : allDeposits) {
            if (deposit.getDepositDate() != null && deposit.getDepositDate().startsWith(monthPrefix)) {
                monthlyDeposits.add(deposit);
                totalCollection += deposit.getAmount();
                
                // Track unique customers
                uniqueCustomers.put(deposit.getCustomerId(), true);
                
                // Create daily summary
                String date = deposit.getDepositDate();
                if (dailySummaryMap.containsKey(date)) {
                    ReportData.DailySummary summary = dailySummaryMap.get(date);
                    summary.setTotalAmount(summary.getTotalAmount() + deposit.getAmount());
                    summary.setEntryCount(summary.getEntryCount() + 1);
                } else {
                    dailySummaryMap.put(date, new ReportData.DailySummary(date, deposit.getAmount(), 1));
                }
            }
        }
        
        // Determine total customers: count all active customers provided (not just those with deposits)
        int activeCustomerCount = 0;
        if (allCustomers != null) {
            for (Customer c : allCustomers) {
                if (c != null && c.isActive()) {
                    activeCustomerCount++;
                }
            }
        }

        // Convert to list and sort by date (descending - newest first)
        List<ReportData.DailySummary> dailySummaries = new ArrayList<>(dailySummaryMap.values());
        dailySummaries.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        String monthYearDisplay = monthYearFormat.format(cal.getTime());
        
        return new ReportData.MonthlyReport(monthYearDisplay, yearStr, totalCollection, 
                                          monthlyDeposits.size(), activeCustomerCount, dailySummaries);
    }
    
    /**
     * Generate Customer Report
     */
    public List<ReportData.CustomerReport> generateCustomerReports(List<Deposit> allDeposits, 
                                                                  List<Customer> allCustomers) {
        List<ReportData.CustomerReport> customerReports = new ArrayList<>();
        
        for (Customer customer : allCustomers) {
            List<Deposit> customerDeposits = new ArrayList<>();
            double totalAmount = 0.0;
            String firstEntryDate = null;
            String lastEntryDate = null;
            
            for (Deposit deposit : allDeposits) {
                if (deposit.getCustomerId() == customer.getId()) {
                    customerDeposits.add(deposit);
                    totalAmount += deposit.getAmount();
                    
                    if (firstEntryDate == null || deposit.getDepositDate().compareTo(firstEntryDate) < 0) {
                        firstEntryDate = deposit.getDepositDate();
                    }
                    if (lastEntryDate == null || deposit.getDepositDate().compareTo(lastEntryDate) > 0) {
                        lastEntryDate = deposit.getDepositDate();
                    }
                }
            }
            
            if (!customerDeposits.isEmpty()) {
                customerReports.add(new ReportData.CustomerReport(customer, totalAmount, 
                    customerDeposits.size(), firstEntryDate, lastEntryDate, customerDeposits));
            }
        }
        
        return customerReports;
    }
    
    /**
     * Generate Daily Report Receipt Text
     */
    public String generateDailyReportReceipt(ReportData.DailyReport report) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=====================================\n");
        receipt.append("        DAILY COLLECTION REPORT      \n");
        receipt.append("=====================================\n");
        receipt.append("Date: ").append(formatDateForDisplay(report.getDate())).append("\n");
        receipt.append("Generated: ").append(displayDateFormat.format(new Date())).append("\n");
        receipt.append("-------------------------------------\n");
        receipt.append("SUMMARY:\n");
        receipt.append("Total Entries: ").append(report.getTotalEntries()).append("\n");
        receipt.append("Total Collection: Rs. ").append(String.format(Locale.getDefault(), "%.2f", report.getTotalCollection())).append("\n");
        receipt.append("-------------------------------------\n");
        receipt.append("DETAILED ENTRIES:\n");
        
        for (Deposit deposit : report.getDeposits()) {
            receipt.append("Account Number: ").append(deposit.getAccountNumber() != null ? deposit.getAccountNumber() : "N/A").append("\n");
            receipt.append("Amount: Rs. ").append(String.format(Locale.getDefault(), "%.2f", deposit.getAmount())).append("\n");
            receipt.append("Time: ").append(deposit.getDepositTime()).append("\n");
            if (deposit.getNotes() != null && !deposit.getNotes().isEmpty()) {
                receipt.append("Remarks: ").append(deposit.getNotes()).append("\n");
            }
            receipt.append("- - - - - - - - - - - - - - - - - - -\n");
        }
        
        receipt.append("=====================================\n");
        receipt.append("       Thank you for using          \n");
        receipt.append("         SmartBhishi App             \n");
        receipt.append("=====================================\n");
        
        return receipt.toString();
    }
    
    /**
     * Generate Monthly Report Receipt Text
     */
    public String generateMonthlyReportReceipt(ReportData.MonthlyReport report) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=====================================\n");
        receipt.append("       MONTHLY COLLECTION REPORT     \n");
        receipt.append("=====================================\n");
        receipt.append("Month: ").append(report.getMonth()).append("\n");
        receipt.append("Generated: ").append(displayDateFormat.format(new Date())).append("\n");
        receipt.append("-------------------------------------\n");
        receipt.append("SUMMARY:\n");
        receipt.append("Total Entries: ").append(report.getTotalEntries()).append("\n");
        receipt.append("Total Collection: Rs. ").append(String.format(Locale.getDefault(), "%.2f", report.getTotalCollection())).append("\n");
        receipt.append("Total Customers: ").append(report.getTotalCustomers()).append("\n");
        receipt.append("-------------------------------------\n");
        receipt.append("DAILY BREAKDOWN:\n");
        
        for (ReportData.DailySummary summary : report.getDailySummaries()) {
            receipt.append("Date: ").append(formatDateForDisplay(summary.getDate())).append("\n");
            receipt.append("Entries: ").append(summary.getEntryCount()).append("\n");
            receipt.append("Amount: Rs. ").append(String.format(Locale.getDefault(), "%.2f", summary.getTotalAmount())).append("\n");
            receipt.append("- - - - - - - - - - - - - - - - - - -\n");
        }
        
        receipt.append("=====================================\n");
        receipt.append("       Thank you for using          \n");
        receipt.append("         SmartBhishi App             \n");
        receipt.append("=====================================\n");
        
        return receipt.toString();
    }
    
    /**
     * Generate Weekly Report Receipt Text
     */
    public String generateWeeklyReportReceipt(ReportData.WeeklyReport report) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=====================================\n");
        receipt.append("         WEEKLY REPORT               \n");
        receipt.append("=====================================\n");
        receipt.append("Week: ").append(report.getWeekPeriod()).append("\n");
        receipt.append("Generated: ").append(displayDateFormat.format(new Date())).append("\n");
        receipt.append("-------------------------------------\n");
        receipt.append("SUMMARY:\n");
        receipt.append("Total Entries: ").append(report.getTotalEntries()).append("\n");
        receipt.append("Total Collection: Rs. ").append(String.format(Locale.getDefault(), "%.2f", report.getTotalCollection())).append("\n");
        receipt.append("-------------------------------------\n");
        receipt.append("DAILY BREAKDOWN:\n");
        
        for (ReportData.DailySummary summary : report.getDailySummaries()) {
            receipt.append("Date: ").append(formatDateForDisplay(summary.getDate())).append("\n");
            receipt.append("Entries: ").append(summary.getEntryCount()).append("\n");
            receipt.append("Amount: Rs. ").append(String.format(Locale.getDefault(), "%.2f", summary.getTotalAmount())).append("\n");
            receipt.append("- - - - - - - - - - - - - - - - - - -\n");
        }
        
        receipt.append("=====================================\n");
        receipt.append("       Thank you for using          \n");
        receipt.append("         SmartBhishi App             \n");
        receipt.append("=====================================\n");
        
        return receipt.toString();
    }
    
    /**
     * Save Weekly Report to File
     */
    public String saveWeeklyReportToFile(ReportData.WeeklyReport report) {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            
            String filename = "WeeklyReport_" + report.getWeekPeriod().replace("/", "-").replace(" - ", "_to_") + ".txt";
            File file = new File(downloadsDir, filename);
            
            FileWriter writer = new FileWriter(file);
            writer.write(generateWeeklyReportReceipt(report));
            writer.close();
            
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Generate Customer Report Receipt Text
     */
    public String generateCustomerReportReceipt(ReportData.CustomerReport report) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=====================================\n");
        receipt.append("        CUSTOMER REPORT              \n");
        receipt.append("=====================================\n");
        receipt.append("Customer: ").append(report.getCustomer().getName()).append("\n");
        receipt.append("Mobile: ").append(report.getCustomer().getPhoneNumber()).append("\n");
        receipt.append("Address: ").append(report.getCustomer().getAddress()).append("\n");
        receipt.append("Generated: ").append(displayDateFormat.format(new Date())).append("\n");
        receipt.append("-------------------------------------\n");
        receipt.append("SUMMARY:\n");
        receipt.append("Total Entries: ").append(report.getTotalEntries()).append("\n");
        receipt.append("Total Amount: Rs. ").append(String.format(Locale.getDefault(), "%.2f", report.getTotalAmount())).append("\n");
        receipt.append("First Entry: ").append(formatDateForDisplay(report.getFirstEntryDate())).append("\n");
        receipt.append("Last Entry: ").append(formatDateForDisplay(report.getLastEntryDate())).append("\n");
        receipt.append("-------------------------------------\n");
        receipt.append("TRANSACTION HISTORY:\n");
        
        for (Deposit deposit : report.getDeposits()) {
            receipt.append("Date: ").append(formatDateForDisplay(deposit.getDepositDate())).append("\n");
            receipt.append("Time: ").append(deposit.getDepositTime()).append("\n");
            receipt.append("Amount: Rs. ").append(String.format(Locale.getDefault(), "%.2f", deposit.getAmount())).append("\n");
            if (deposit.getNotes() != null && !deposit.getNotes().isEmpty()) {
                receipt.append("Remarks: ").append(deposit.getNotes()).append("\n");
            }
            receipt.append("- - - - - - - - - - - - - - - - - - -\n");
        }
        
        receipt.append("=====================================\n");
        receipt.append("       Thank you for using          \n");
        receipt.append("         SmartBhishi App             \n");
        receipt.append("=====================================\n");
        
        return receipt.toString();
    }
    
    /**
     * Save report to file
     */
    public File saveReportToFile(String reportContent, String fileName) throws IOException {
        File documentsDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "SmartBhishi_Reports");
        if (!documentsDir.exists()) {
            documentsDir.mkdirs();
        }
        
        File reportFile = new File(documentsDir, fileName + ".txt");
        FileWriter writer = new FileWriter(reportFile);
        writer.write(reportContent);
        writer.close();
        
        return reportFile;
    }
    
    /**
     * Format date for display
     */
    private String formatDateForDisplay(String dateString) {
        try {
            Date date = dateFormat.parse(dateString);
            return displayDateFormat.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }
    
    /**
     * Get current date string
     */
    public String getCurrentDateString() {
        return dateFormat.format(new Date());
    }
    
    /**
     * Get current month and year
     */
    public int[] getCurrentMonthYear() {
        Calendar cal = Calendar.getInstance();
        return new int[]{cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)};
    }
}
