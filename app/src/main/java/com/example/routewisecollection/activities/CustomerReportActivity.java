package com.example.routewisecollection.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routewisecollection.R;
import com.example.routewisecollection.adapters.CustomerReportAdapter;
import com.example.routewisecollection.models.CustomerReportModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomerReportActivity extends AppCompatActivity {

    private EditText etSearchCustomer;
    private TextView tvTotalCustomers, tvNoCustomers,tvTotalAmount;
    private RecyclerView rvCustomerReports;

    private List<CustomerReportModel> customerReports = new ArrayList<>();
    private List<CustomerReportModel> filteredReports = new ArrayList<>();

    private CustomerReportAdapter adapter;

    private String agentId = "7058606098";
    private DatabaseReference customersRef, transactionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_report);

        etSearchCustomer = findViewById(R.id.etSearchCustomer);
        tvTotalCustomers = findViewById(R.id.tvTotalCustomers);
        tvNoCustomers = findViewById(R.id.tvNoCustomers);
        rvCustomerReports = findViewById(R.id.rvCustomerReports);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        customersRef = FirebaseDatabase.getInstance().getReference("agents").child(agentId).child("customers");
        transactionsRef = FirebaseDatabase.getInstance().getReference("agents").child(agentId).child("transactions");

        adapter = new CustomerReportAdapter(filteredReports, this);
        rvCustomerReports.setLayoutManager(new LinearLayoutManager(this));
        rvCustomerReports.setAdapter(adapter);

        etSearchCustomer.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterReports(s.toString());
            }
        });

        loadData();
    }

    private void loadData() {
        // Load customers then transactions to build report list
        customersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot customerSnap) {
                Map<String, CustomerReportModel> customerMap = new HashMap<>();

                for (DataSnapshot cSnap : customerSnap.getChildren()) {
                    String name = cSnap.child("name").getValue(String.class);
                    String phone = cSnap.getKey(); // Assuming phone is key
                    String address = cSnap.child("address").getValue(String.class);

                    CustomerReportModel crm = new CustomerReportModel(
                            name, phone, address,
                            0, "", 0.0);
                    customerMap.put(phone, crm);
                }

                // Load transactions now for aggregation
                transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot transactionSnap) {
                        for (DataSnapshot custTransSnap : transactionSnap.getChildren()) {
                            String customerId = custTransSnap.getKey(); // phone number
                            CustomerReportModel crm = customerMap.get(customerId);
                            if (crm != null) {
                                List<Date> dates = new ArrayList<>();
                                int totalEntries = 0;
                                double totalAmount = 0.0;

                                for (DataSnapshot transSnap : custTransSnap.getChildren()) {
                                    totalEntries++;
                                    Double amount = transSnap.child("totalAmount").getValue(Double.class);
                                    totalAmount += (amount != null) ? amount : 0;

                                    String dateStr = transSnap.child("depositDate").getValue(String.class);
                                    if (dateStr != null) {
                                        try {
                                            dates.add(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr));
                                        } catch (ParseException ignored) {}
                                    }
                                }

                                crm.setTotalEntries(totalEntries);
                                crm.setTotalAmount(totalAmount);

                                if (!dates.isEmpty()) {
                                    Collections.sort(dates);
                                    SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMM", Locale.getDefault());
                                    String dateRange = sdfOut.format(dates.get(0)) + " - " + sdfOut.format(dates.get(dates.size() - 1));
                                    crm.setDateRange(dateRange);
                                } else {
                                    crm.setDateRange("No Date");
                                }
                            }
                        }
                        customerReports = new ArrayList<>(customerMap.values());
                        filteredReports.clear();
                        filteredReports.addAll(customerReports);
                        updateUI();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CustomerReportActivity.this, "Transaction load failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerReportActivity.this, "Customer load failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterReports(String query) {
        String lowerQuery = query.toLowerCase();
        filteredReports.clear();

        if (query.isEmpty()) {
            filteredReports.addAll(customerReports);
        } else {
            for (CustomerReportModel cr : customerReports) {
                if (cr.getName() != null && cr.getName().toLowerCase().contains(lowerQuery)) {
                    filteredReports.add(cr);
                } else if (cr.getPhoneNumber() != null && cr.getPhoneNumber().contains(query)) {
                    filteredReports.add(cr);
                }
            }
        }
        updateUI();
    }

    private void updateUI() {
        tvTotalCustomers.setText(String.valueOf(filteredReports.size()));

        // Calculate and display total amount
        double totalAmount = 0.0;
        for (CustomerReportModel customer : filteredReports) {
            totalAmount += customer.getTotalAmount();
        }
        tvTotalAmount.setText(String.format(Locale.getDefault(), "₹%.2f", totalAmount));

        if (filteredReports.isEmpty()) {
            rvCustomerReports.setVisibility(android.view.View.GONE);
            tvNoCustomers.setVisibility(android.view.View.VISIBLE);
        } else {
            rvCustomerReports.setVisibility(android.view.View.VISIBLE);
            tvNoCustomers.setVisibility(android.view.View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

}
