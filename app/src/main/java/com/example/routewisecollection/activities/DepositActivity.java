package com.example.routewisecollection.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.routewisecollection.R;
import com.example.routewisecollection.models.Customer;
import com.example.routewisecollection.models.Deposit;
import com.example.routewisecollection.utils.Constants;
import com.example.routewisecollection.utils.InterestCalculator;
import com.example.routewisecollection.utils.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DepositActivity extends AppCompatActivity {

    private Spinner spinnerCustomer;
    private EditText etDepositAmount;
    private EditText etSearchCustomer;
    private TextView tvCustomerDetails, tvInterestAmount, tvTotalAmount;
    private Button btnCalculate, btnSaveDeposit, btnCancel;
    private List<Customer> customerList = new ArrayList<>();
    private List<Customer> displayedCustomers = new ArrayList<>();
    private ArrayAdapter<String> customerNamesAdapter;
    private Customer selectedCustomer;

    private DatabaseReference customersRef;
    private DatabaseReference transactionsRef;
    private String agentMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        // Initialize views
        spinnerCustomer = findViewById(R.id.spinnerCustomer);
        etSearchCustomer = findViewById(R.id.etSearchCustomer);
        etDepositAmount = findViewById(R.id.etDepositAmount);
        tvCustomerDetails = findViewById(R.id.tvCustomerDetails);
        tvInterestAmount = findViewById(R.id.tvInterestAmount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnSaveDeposit = findViewById(R.id.btnSaveDeposit);
        btnCancel = findViewById(R.id.btnCancelDeposit);

        // Get agent mobile from LoginManager session
        LoginManager loginManager = new LoginManager(this);
        agentMobile = loginManager.getAgentMobile();

        // Initialize Firebase references
        customersRef = FirebaseDatabase.getInstance()
                .getReference("agents")
                .child(agentMobile)
                .child("customers");

        transactionsRef = FirebaseDatabase.getInstance()
                .getReference("agents")
                .child(agentMobile)
                .child("transactions");

        fetchCustomersFromFirebase();
        setupClickListeners();
        setupSearch();
    }

    private void fetchCustomersFromFirebase() {
        customersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customerList.clear();
                List<String> customerNames = new ArrayList<>();
                for (DataSnapshot customerSnapshot : snapshot.getChildren()) {
                    Customer customer = customerSnapshot.getValue(Customer.class);
                    if (customer != null) {
                        customerList.add(customer);
                        customerNames.add(customer.getName() + " - " + customer.getAccountNumber());
                    }
                }
                customerNamesAdapter = new ArrayAdapter<>(DepositActivity.this, android.R.layout.simple_spinner_item, customerNames);
                customerNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCustomer.setAdapter(customerNamesAdapter);

                if (!customerList.isEmpty()) {
                    updateCustomerSpinner(etSearchCustomer.getText() != null ? etSearchCustomer.getText().toString() : "");
                } else {
                    selectedCustomer = null;
                    tvCustomerDetails.setText("No customers found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DepositActivity.this, "Failed to load customers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        if (etSearchCustomer == null) return;
        etSearchCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCustomerSpinner(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void updateCustomerSpinner(String query) {
        if (customerList == null || customerList.isEmpty() || customerNamesAdapter == null) return;
        String q = query == null ? "" : query.trim().toLowerCase();
        List<Customer> sorted = new ArrayList<>(customerList);
        if (!q.isEmpty()) {
            Collections.sort(sorted, new Comparator<Customer>() {
                @Override
                public int compare(Customer a, Customer b) {
                    int sa = scoreCustomer(a, q);
                    int sb = scoreCustomer(b, q);
                    return Integer.compare(sb, sa);
                }
            });
        }
        displayedCustomers = sorted;
        List<String> names = new ArrayList<>();
        for (Customer c : sorted) {
            names.add(c.getName() + " - " + c.getAccountNumber());
        }
        customerNamesAdapter.clear();
        customerNamesAdapter.addAll(names);
        customerNamesAdapter.notifyDataSetChanged();
        if (!sorted.isEmpty()) {
            spinnerCustomer.setSelection(0);
            selectedCustomer = displayedCustomers.get(0);
            displayCustomerDetails();
        }
    }

    private int scoreCustomer(Customer c, String q) {
        String name = c.getName() != null ? c.getName().toLowerCase() : "";
        String acc = c.getAccountNumber() != null ? c.getAccountNumber().toLowerCase() : "";
        String ph = c.getPhoneNumber() != null ? c.getPhoneNumber().toLowerCase() : "";
        int score = 0;
        if (name.startsWith(q)) score += 100;
        else if (name.contains(q)) score += 60;
        if (acc.startsWith(q)) score += 90;
        else if (acc.contains(q)) score += 50;
        if (ph.startsWith(q)) score += 80;
        else if (ph.contains(q)) score += 40;
        return score;
    }

    private void setupClickListeners() {
        btnCalculate.setOnClickListener(v -> calculateInterest());
        btnSaveDeposit.setOnClickListener(v -> saveDeposit());
        btnCancel.setOnClickListener(v -> finish());

        spinnerCustomer.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position >= 0 && position < displayedCustomers.size()) {
                    selectedCustomer = displayedCustomers.get(position);
                    displayCustomerDetails();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedCustomer = null;
                tvCustomerDetails.setText("");
                tvInterestAmount.setText("");
                tvTotalAmount.setText("");
            }
        });
    }

    private void displayCustomerDetails() {
        if (selectedCustomer != null) {
            String details = "Name: " + selectedCustomer.getName() + "\n" +
                    "Account: " + selectedCustomer.getAccountNumber() + "\n" +
                    "Principal: Rs. " + selectedCustomer.getPrincipalAmount() + "\n" +
                    "Interest Rate: " + selectedCustomer.getInterestRate() + "%";
            tvCustomerDetails.setText(details);
        }
    }

    private void calculateInterest() {
        String amountStr = etDepositAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter deposit amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCustomer == null) {
            Toast.makeText(this, "Please select a customer", Toast.LENGTH_SHORT).show();
            return;
        }

        double depositAmount = Double.parseDouble(amountStr);
        double interestRate = selectedCustomer.getInterestRate();
        double interestAmount = InterestCalculator.calculateInterestOnDeposit(depositAmount, interestRate);
        double totalAmount = depositAmount + interestAmount;

        tvInterestAmount.setText("Interest: Rs. " + String.format(Locale.getDefault(), "%.2f", interestAmount));
        tvTotalAmount.setText("Total: Rs. " + String.format(Locale.getDefault(), "%.2f", totalAmount));
    }

    private void saveDeposit() {
        String amountStr = etDepositAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter deposit amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedCustomer == null) {
            Toast.makeText(this, "Please select a customer", Toast.LENGTH_SHORT).show();
            return;
        }

        double depositAmount = Double.parseDouble(amountStr);
        double interestRate = selectedCustomer.getInterestRate();
        double interestAmount = InterestCalculator.calculateInterestOnDeposit(depositAmount, interestRate);
        double totalAmount = depositAmount + interestAmount;

        SimpleDateFormat dateSdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        SimpleDateFormat timeSdf = new SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault());
        Date now = new Date();
        String depositDate = dateSdf.format(now);
        String depositTime = timeSdf.format(now);

        String receiptNumber = generateReceiptNumber();

        Deposit deposit = new Deposit(selectedCustomer.getId(), selectedCustomer.getAccountNumber(), 
                depositAmount, depositDate, depositTime, interestAmount, totalAmount, 
                "", receiptNumber, "", selectedCustomer.getName());

        // Save deposit transaction under /agents/{agentMobile}/transactions/{customerPhone}/unique_push_id
        DatabaseReference customerTransactionRef = transactionsRef.child(selectedCustomer.getPhoneNumber()).push();

        customerTransactionRef.setValue(deposit)
                .addOnSuccessListener(unused -> Toast.makeText(DepositActivity.this, "Deposit saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(DepositActivity.this, "Failed to save deposit: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        // Optionally clear inputs or finish activity
        finish();
    }

    private String generateReceiptNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Constants.RECEIPT_PREFIX + sdf.format(new Date());
    }
}
