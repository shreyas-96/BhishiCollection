package com.example.routewisecollection.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routewisecollection.R;
import com.example.routewisecollection.adapters.DailySummaryAdapter;
import com.example.routewisecollection.models.ReportData;
import com.example.routewisecollection.utils.LoginManager;
import com.example.routewisecollection.utils.ReportManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeeklyReportActivity extends AppCompatActivity {

    private Button btnSelectWeek;
    private TextView tvWeekPeriod, tvTotalCollection, tvTotalEntries;
    private CardView summaryCard, dailyBreakdownCard;
    private LinearLayout actionButtons;
    private RecyclerView recyclerViewDailySummary;
    private Button btnSaveReport, btnPrintReport;

    private DailySummaryAdapter dailySummaryAdapter;
    private DatabaseReference transactionsRef;
    private String agentMobile;
    private Calendar selectedWeekStart;
    private ReportData.WeeklyReport currentWeeklyReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_report);

        initializeViews();
        setupFirebase();
        setupClickListeners();
        setupRecyclerView();
    }

    private void initializeViews() {
        btnSelectWeek = findViewById(R.id.btnSelectWeek);
        tvWeekPeriod = findViewById(R.id.tvWeekPeriod);
        tvTotalCollection = findViewById(R.id.tvTotalCollection);
        tvTotalEntries = findViewById(R.id.tvTotalEntries);
        summaryCard = findViewById(R.id.summaryCard);
        dailyBreakdownCard = findViewById(R.id.dailyBreakdownCard);
        actionButtons = findViewById(R.id.actionButtons);
        recyclerViewDailySummary = findViewById(R.id.recyclerViewDailySummary);
        btnSaveReport = findViewById(R.id.btnSaveReport);
        btnPrintReport = findViewById(R.id.btnPrintReport);
    }

    private void setupFirebase() {
        LoginManager loginManager = new LoginManager(this);
        agentMobile = loginManager.getAgentMobile();
        transactionsRef = FirebaseDatabase.getInstance()
                .getReference("agents").child(agentMobile).child("transactions");
    }

    private void setupClickListeners() {
        btnSelectWeek.setOnClickListener(v -> showWeekPicker());
        btnSaveReport.setOnClickListener(v -> saveReport());
        btnPrintReport.setOnClickListener(v -> printReport());
    }

    private void setupRecyclerView() {
        dailySummaryAdapter = new DailySummaryAdapter();
        recyclerViewDailySummary.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDailySummary.setAdapter(dailySummaryAdapter);
    }

    private void showWeekPicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    
                    // Find the start of the week (Monday)
                    selectedWeekStart = Calendar.getInstance();
                    selectedWeekStart.setTime(selected.getTime());
                    int dayOfWeek = selectedWeekStart.get(Calendar.DAY_OF_WEEK);
                    int daysToSubtract = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
                    selectedWeekStart.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
                    
                    loadWeeklyReport();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        
        datePickerDialog.setTitle("Select any day in the week");
        datePickerDialog.show();
    }

    private void loadWeeklyReport() {
        if (selectedWeekStart == null) return;

        Calendar weekEnd = Calendar.getInstance();
        weekEnd.setTime(selectedWeekStart.getTime());
        weekEnd.add(Calendar.DAY_OF_MONTH, 6);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = sdf.format(selectedWeekStart.getTime());
        String endDate = sdf.format(weekEnd.getTime());

        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<com.example.routewisecollection.models.Deposit> weeklyTransactions = new ArrayList<>();
                
                for (DataSnapshot customerSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot transactionSnapshot : customerSnapshot.getChildren()) {
                        com.example.routewisecollection.models.Deposit transaction = 
                            transactionSnapshot.getValue(com.example.routewisecollection.models.Deposit.class);
                        
                        if (transaction != null && transaction.getDepositDate() != null) {
                            String txDate = transaction.getDepositDate();
                            if (txDate.compareTo(startDate) >= 0 && txDate.compareTo(endDate) <= 0) {
                                weeklyTransactions.add(transaction);
                            }
                        }
                    }
                }

                generateWeeklyReport(weeklyTransactions, startDate, endDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeeklyReportActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateWeeklyReport(List<com.example.routewisecollection.models.Deposit> transactions, String startDate, String endDate) {
        ReportManager reportManager = new ReportManager(this);
        currentWeeklyReport = reportManager.generateWeeklyReport(transactions, startDate, endDate);

        displayWeeklyReport();
    }

    private void displayWeeklyReport() {
        if (currentWeeklyReport == null) return;

        // Update period display
        tvWeekPeriod.setText("Week: " + currentWeeklyReport.getWeekPeriod());
        
        // Update summary
        tvTotalCollection.setText(String.format(Locale.getDefault(), "₹%.2f", currentWeeklyReport.getTotalCollection()));
        tvTotalEntries.setText(String.valueOf(currentWeeklyReport.getTotalEntries()));

        // Update daily breakdown
        dailySummaryAdapter.setDailySummaries(currentWeeklyReport.getDailySummaries());

        // Show cards and buttons
        summaryCard.setVisibility(View.VISIBLE);
        dailyBreakdownCard.setVisibility(View.VISIBLE);
        actionButtons.setVisibility(View.VISIBLE);
    }

    private void saveReport() {
        if (currentWeeklyReport == null) {
            Toast.makeText(this, "No report to save", Toast.LENGTH_SHORT).show();
            return;
        }

        ReportManager reportManager = new ReportManager(this);
        String filename = reportManager.saveWeeklyReportToFile(currentWeeklyReport);
        
        if (filename != null) {
            Toast.makeText(this, "Report saved: " + filename, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save report", Toast.LENGTH_SHORT).show();
        }
    }

    private void printReport() {
        if (currentWeeklyReport == null) {
            Toast.makeText(this, "No report to print", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PrintReceiptActivity.class);
        intent.putExtra("report_type", "weekly");
        intent.putExtra("week_period", currentWeeklyReport.getWeekPeriod());
        intent.putExtra("total_collection", currentWeeklyReport.getTotalCollection());
        intent.putExtra("total_entries", currentWeeklyReport.getTotalEntries());
        startActivity(intent);
    }
}
