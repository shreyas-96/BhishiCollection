package com.example.routewisecollection.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import com.example.routewisecollection.adapters.DailyEntryAdapter;
import com.example.routewisecollection.models.Deposit;
import com.example.routewisecollection.utils.LoginManager;
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

public class DailyReportActivity extends AppCompatActivity {

    private TextView tvSelectedDate, tvTotalEntries, tvTotalCollection, tvNoEntries;
    private CardView btnSelectDate, btnPrintReport;
    private RecyclerView rvDailyEntries;
    private DailyEntryAdapter adapter;

    private String selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private String agentId;
    private List<Deposit> currentDeposits = new ArrayList<>();
    private DatabaseReference transactionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);

        // Get agent mobile from LoginManager
        LoginManager loginManager = new LoginManager(this);
        agentId = loginManager.getAgentMobile();

        // Initialize Firebase reference
        transactionsRef = FirebaseDatabase.getInstance()
                .getReference("agents")
                .child(agentId)
                .child("transactions");

        initializeViews();
        setupRecyclerView();
        setupClickListeners();

        selectedDate = dateFormat.format(new Date());
        updateSelectedDateDisplay();
        fetchTodaysDeposits();
    }

    private void initializeViews() {
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvTotalEntries = findViewById(R.id.tvTotalEntries);
        tvTotalCollection = findViewById(R.id.tvTotalCollection);
        tvNoEntries = findViewById(R.id.tvNoEntries);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnPrintReport = findViewById(R.id.btnPrintReport);
        rvDailyEntries = findViewById(R.id.rvDailyEntries);

        adapter = new DailyEntryAdapter(new ArrayList<>());
    }

    private void setupRecyclerView() {
        rvDailyEntries.setLayoutManager(new LinearLayoutManager(this));
        rvDailyEntries.setAdapter(adapter);
        
        // Set delete click listener
        adapter.setOnDeleteClickListener((deposit, position) -> showDeleteConfirmationDialog(deposit, position));
    }

    private void setupClickListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnPrintReport.setOnClickListener(v -> saveAndPrintReport());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = dateFormat.parse(selectedDate);
            calendar.setTime(date);
        } catch (Exception e) {
            // Use current date if parsing fails
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);
                    selectedDate = dateFormat.format(selectedCalendar.getTime());
                    updateSelectedDateDisplay();
                    fetchTodaysDeposits();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void updateSelectedDateDisplay() {
        try {
            Date date = dateFormat.parse(selectedDate);
            String displayDate = displayDateFormat.format(date);

            String today = dateFormat.format(new Date());
            if (selectedDate.equals(today)) {
                tvSelectedDate.setText("Today (" + displayDate + ")");
            } else {
                tvSelectedDate.setText(displayDate);
            }
        } catch (Exception e) {
            tvSelectedDate.setText(selectedDate);
        }
    }

    // Fetch deposits for selected date (only today's date entries)
    private void fetchTodaysDeposits() {
        currentDeposits.clear();

        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot custTxSnap) {
                for (DataSnapshot customerSnap : custTxSnap.getChildren()) {
                    for (DataSnapshot depositSnap : customerSnap.getChildren()) {
                        Deposit deposit = depositSnap.getValue(Deposit.class);
                        if (deposit != null && selectedDate.equals(deposit.getDepositDate())) {
                            // Store the Firebase key with the deposit
                            deposit.setId(depositSnap.getKey().hashCode());
                            currentDeposits.add(deposit);
                        }
                    }
                }
                updateUI(currentDeposits);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyReportActivity.this, "Failed to load deposits: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(List<Deposit> deposits) {
        Log.d("DailyReport", "Deposits fetched: " + (deposits == null ? 0 : deposits.size()));

        tvTotalEntries.setText(String.valueOf(deposits.size()));

        double totalAmount = 0.0;
        for (Deposit d : deposits) {
            totalAmount += d.getAmount();
        }
        tvTotalCollection.setText("₹" + String.format(Locale.getDefault(), "%.2f", totalAmount));

        if (deposits.isEmpty()) {
            rvDailyEntries.setVisibility(View.GONE);
            tvNoEntries.setVisibility(View.VISIBLE);
        } else {
            rvDailyEntries.setVisibility(View.VISIBLE);
            tvNoEntries.setVisibility(View.GONE);
            adapter.updateDeposits(deposits);
        }
    }

    private void showDeleteConfirmationDialog(Deposit deposit, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this deposit entry?\n\nAccount: " + 
                        deposit.getAccountNumber() + "\nAmount: ₹" + 
                        String.format(Locale.getDefault(), "%.2f", deposit.getAmount()))
                .setPositiveButton("Delete", (dialog, which) -> deleteDeposit(deposit, position))
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteDeposit(Deposit deposit, int position) {
        // Find and delete from Firebase
        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot custTxSnap) {
                boolean deleted = false;
                for (DataSnapshot customerSnap : custTxSnap.getChildren()) {
                    for (DataSnapshot depositSnap : customerSnap.getChildren()) {
                        Deposit fbDeposit = depositSnap.getValue(Deposit.class);
                        if (fbDeposit != null && 
                            fbDeposit.getDepositDate().equals(deposit.getDepositDate()) &&
                            fbDeposit.getDepositTime().equals(deposit.getDepositTime()) &&
                            fbDeposit.getAmount() == deposit.getAmount() &&
                            fbDeposit.getAccountNumber() != null &&
                            fbDeposit.getAccountNumber().equals(deposit.getAccountNumber())) {
                            
                            // Delete from Firebase
                            depositSnap.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(DailyReportActivity.this, "Entry deleted successfully", Toast.LENGTH_SHORT).show();
                                        // Refresh data from Firebase to ensure UI is in sync
                                        fetchTodaysDeposits();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(DailyReportActivity.this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                            deleted = true;
                            break;
                        }
                    }
                    if (deleted) break;
                }
                if (!deleted) {
                    Toast.makeText(DailyReportActivity.this, "Entry not found in database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyReportActivity.this, "Failed to delete: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAndPrintReport() {
        // Implement save and print function as needed
        Toast.makeText(this, "Save & Print functionality not implemented", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
