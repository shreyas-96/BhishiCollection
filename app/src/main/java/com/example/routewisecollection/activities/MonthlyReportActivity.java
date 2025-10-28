package com.example.routewisecollection.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routewisecollection.R;
import com.example.routewisecollection.adapters.DailySummaryAdapter;
import com.example.routewisecollection.models.Customer;
import com.example.routewisecollection.models.Deposit;
import com.example.routewisecollection.models.ReportData;
import com.example.routewisecollection.utils.LoginManager;
import com.example.routewisecollection.utils.ReportManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonthlyReportActivity extends AppCompatActivity {

    private TextView tvSelectedMonth, tvTotalEntries, tvTotalCollection, tvTotalCustomers, tvNoData;
    private CardView btnSelectMonth, btnPrintReport;
    private RecyclerView rvDailySummary;

    private ReportManager reportManager;
    private DailySummaryAdapter adapter;
    private ReportData.MonthlyReport currentReport;

    private int selectedMonth, selectedYear;
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    private DatabaseReference transactionsRef, customersRef;
    private String agentMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Monthly Report");
        }

        initializeViews();
        setupFirebase();
        setupClickListeners();
        setupRecyclerView();

        // Set current month as default
        Calendar cal = Calendar.getInstance();
        selectedMonth = cal.get(Calendar.MONTH) + 1;
        selectedYear = cal.get(Calendar.YEAR);
        updateSelectedMonthDisplay();
        loadMonthlyReport();
    }

    private void initializeViews() {
        tvSelectedMonth = findViewById(R.id.tvSelectedMonth);
        tvTotalEntries = findViewById(R.id.tvTotalEntries);
        tvTotalCollection = findViewById(R.id.tvTotalCollection);
        tvTotalCustomers = findViewById(R.id.tvTotalCustomers);
        tvNoData = findViewById(R.id.tvNoData);
        btnSelectMonth = findViewById(R.id.btnSelectMonth);
        btnPrintReport = findViewById(R.id.btnPrintReport);
        rvDailySummary = findViewById(R.id.rvDailySummary);

        reportManager = new ReportManager(this);
    }

    private void setupFirebase() {
        LoginManager loginManager = new LoginManager(this);
        agentMobile = loginManager.getAgentMobile();

        DatabaseReference agentRef = FirebaseDatabase.getInstance()
                .getReference("agents")
                .child(agentMobile);

        transactionsRef = agentRef.child("transactions");
        customersRef = agentRef.child("customers");
    }

    private void setupClickListeners() {
        btnSelectMonth.setOnClickListener(v -> showMonthYearPicker());
        btnPrintReport.setOnClickListener(v -> saveAndPrintReport());
    }

    private void setupRecyclerView() {
        adapter = new DailySummaryAdapter(new ArrayList<>());
        rvDailySummary.setLayoutManager(new LinearLayoutManager(this));
        rvDailySummary.setAdapter(adapter);
    }

    private void showMonthYearPicker() {
        String[] months = {"January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Month and Year");

        // Create a simple month selector (you can enhance this with a custom picker)
        String[] monthYearOptions = new String[24]; // Last 2 years
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);

        for (int i = 0; i < 24; i++) {
            int year = currentYear - (i / 12);
            int month = (cal.get(Calendar.MONTH) - (i % 12) + 12) % 12;
            if (month == 0) month = 12;
            monthYearOptions[i] = months[month - 1] + " " + year;
        }

        builder.setItems(monthYearOptions, (dialog, which) -> {
            int year = currentYear - (which / 12);
            int month = (cal.get(Calendar.MONTH) - (which % 12) + 12) % 12;
            if (month == 0) month = 12;

            selectedMonth = month;
            selectedYear = year;
            updateSelectedMonthDisplay();
            loadMonthlyReport();
        });

        builder.show();
    }

    private void updateSelectedMonthDisplay() {
        Calendar cal = Calendar.getInstance();
        cal.set(selectedYear, selectedMonth - 1, 1);
        String displayMonth = monthYearFormat.format(cal.getTime());

        // Check if it's current month
        Calendar current = Calendar.getInstance();
        if (selectedMonth == (current.get(Calendar.MONTH) + 1) &&
            selectedYear == current.get(Calendar.YEAR)) {
            tvSelectedMonth.setText("Current Month (" + displayMonth + ")");
        } else {
            tvSelectedMonth.setText(displayMonth);
        }
    }

    private void loadMonthlyReport() {
        Log.d("MonthlyReport", "Loading report for month: " + selectedMonth + ", year: " + selectedYear);

        // Fetch all deposits from Firebase
        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot transactionsSnapshot) {
                List<Deposit> allDeposits = new ArrayList<>();

                for (DataSnapshot customerSnapshot : transactionsSnapshot.getChildren()) {
                    for (DataSnapshot depositSnapshot : customerSnapshot.getChildren()) {
                        Deposit deposit = depositSnapshot.getValue(Deposit.class);
                        if (deposit != null) {
                            allDeposits.add(deposit);
                        }
                    }
                }

                Log.d("MonthlyReport", "Total deposits in Firebase: " + allDeposits.size());

                // Fetch all customers from Firebase
                customersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot customersSnapshot) {
                        List<Customer> allCustomers = new ArrayList<>();

                        for (DataSnapshot customerSnapshot : customersSnapshot.getChildren()) {
                            Customer customer = customerSnapshot.getValue(Customer.class);
                            if (customer != null) {
                                allCustomers.add(customer);
                            }
                        }

                        Log.d("MonthlyReport", "Total customers in Firebase: " + allCustomers.size());

                        // Generate report
                        currentReport = reportManager.generateMonthlyReport(
                            allDeposits, allCustomers, selectedMonth, selectedYear);

                        Log.d("MonthlyReport", "Report generated - Entries: " + currentReport.getTotalEntries() +
                              ", Collection: " + currentReport.getTotalCollection() +
                              ", Customers: " + currentReport.getTotalCustomers());

                        updateUI();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("MonthlyReport", "Failed to load customers: " + error.getMessage());
                        Toast.makeText(MonthlyReportActivity.this,
                            "Failed to load customers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MonthlyReport", "Failed to load deposits: " + error.getMessage());
                Toast.makeText(MonthlyReportActivity.this,
                    "Failed to load deposits: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (currentReport != null) {
            // Update summary
            tvTotalEntries.setText(String.valueOf(currentReport.getTotalEntries()));
            tvTotalCollection.setText("₹" + String.format(Locale.getDefault(), "%.2f", currentReport.getTotalCollection()));
            tvTotalCustomers.setText(String.valueOf(currentReport.getTotalCustomers()));

            // Update daily summary list
            if (currentReport.getDailySummaries().isEmpty()) {
                rvDailySummary.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            } else {
                rvDailySummary.setVisibility(View.VISIBLE);
                tvNoData.setVisibility(View.GONE);
                adapter.updateSummaries(currentReport.getDailySummaries());
            }
        }
    }

    private void saveAndPrintReport() {
        if (currentReport == null) {
            Toast.makeText(this, "No report data available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String reportContent = reportManager.generateMonthlyReportReceipt(currentReport);
            String fileName = "Monthly_Report_" + selectedYear + "_" + String.format("%02d", selectedMonth);
            File reportFile = reportManager.saveReportToFile(reportContent, fileName);

            Toast.makeText(this, "Report saved to: " + reportFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Navigate to print receipt activity
            Intent intent = new Intent(this, PrintReceiptActivity.class);
            intent.putExtra("RECEIPT_CONTENT", reportContent);
            intent.putExtra("RECEIPT_TITLE", "Monthly Report - " + tvSelectedMonth.getText().toString());
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(this, "Error saving report: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
