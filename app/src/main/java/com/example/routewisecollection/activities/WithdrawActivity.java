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
import com.example.routewisecollection.utils.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WithdrawActivity extends AppCompatActivity {

    private Spinner spinnerCustomerWithdraw;
    private EditText etSearchCustomerWithdraw;
    private EditText etWithdrawAmount;
    private EditText etWithdrawNotes;
    private TextView tvCustomerDetailsWithdraw;
    private Button btnSaveWithdraw, btnCancelWithdraw;

    private final List<Customer> customerList = new ArrayList<>();
    private List<Customer> displayedCustomers = new ArrayList<>();
    private ArrayAdapter<String> customerNamesAdapter;
    private Customer selectedCustomer;

    private DatabaseReference customersRef;
    private DatabaseReference transactionsRef;
    private String agentMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        spinnerCustomerWithdraw = findViewById(R.id.spinnerCustomerWithdraw);
        etSearchCustomerWithdraw = findViewById(R.id.etSearchCustomerWithdraw);
        etWithdrawAmount = findViewById(R.id.etWithdrawAmount);
        etWithdrawNotes = findViewById(R.id.etWithdrawNotes);
        tvCustomerDetailsWithdraw = findViewById(R.id.tvCustomerDetailsWithdraw);
        btnSaveWithdraw = findViewById(R.id.btnSaveWithdraw);
        btnCancelWithdraw = findViewById(R.id.btnCancelWithdraw);

        LoginManager loginManager = new LoginManager(this);
        agentMobile = loginManager.getAgentMobile();

        customersRef = FirebaseDatabase.getInstance()
                .getReference("agents").child(agentMobile).child("customers");
        transactionsRef = FirebaseDatabase.getInstance()
                .getReference("agents").child(agentMobile).child("transactions");

        fetchCustomers();
        setupSearch();
        setupClicks();
    }

    private void fetchCustomers() {
        customersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customerList.clear();
                List<String> names = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Customer c = snap.getValue(Customer.class);
                    if (c != null) {
                        customerList.add(c);
                        names.add(c.getName() + " - " + c.getAccountNumber());
                    }
                }
                customerNamesAdapter = new ArrayAdapter<>(WithdrawActivity.this, android.R.layout.simple_spinner_item, names);
                customerNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCustomerWithdraw.setAdapter(customerNamesAdapter);

                updateCustomerSpinner(etSearchCustomerWithdraw.getText() != null ? etSearchCustomerWithdraw.getText().toString() : "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WithdrawActivity.this, "Failed to load customers: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        etSearchCustomerWithdraw.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateCustomerSpinner(s != null ? s.toString() : ""); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClicks() {
        spinnerCustomerWithdraw.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position >= 0 && position < displayedCustomers.size()) {
                    selectedCustomer = displayedCustomers.get(position);
                    displayCustomerDetails();
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { selectedCustomer = null; tvCustomerDetailsWithdraw.setText(""); }
        });

        btnSaveWithdraw.setOnClickListener(v -> saveWithdraw());
        btnCancelWithdraw.setOnClickListener(v -> finish());
    }

    private void displayCustomerDetails() {
        if (selectedCustomer == null) return;
        String details = "Name: " + selectedCustomer.getName() + "\n" +
                "Account: " + selectedCustomer.getAccountNumber() + "\n" +
                "Mobile: " + selectedCustomer.getPhoneNumber();
        tvCustomerDetailsWithdraw.setText(details);
    }

    private void updateCustomerSpinner(String query) {
        if (customerList.isEmpty() || customerNamesAdapter == null) return;
        String q = query == null ? "" : query.trim().toLowerCase();
        List<Customer> sorted = new ArrayList<>(customerList);
        if (!q.isEmpty()) {
            Collections.sort(sorted, new Comparator<Customer>() {
                @Override
                public int compare(Customer a, Customer b) {
                    return Integer.compare(score(b, q), score(a, q));
                }
            });
        }
        displayedCustomers = sorted;
        List<String> names = new ArrayList<>();
        for (Customer c : sorted) names.add(c.getName() + " - " + c.getAccountNumber());
        customerNamesAdapter.clear();
        customerNamesAdapter.addAll(names);
        customerNamesAdapter.notifyDataSetChanged();
        if (!sorted.isEmpty()) {
            spinnerCustomerWithdraw.setSelection(0);
            selectedCustomer = sorted.get(0);
            displayCustomerDetails();
        }
    }

    private int score(Customer c, String q) {
        String name = c.getName() != null ? c.getName().toLowerCase() : "";
        String acc = c.getAccountNumber() != null ? c.getAccountNumber().toLowerCase() : "";
        String ph = c.getPhoneNumber() != null ? c.getPhoneNumber().toLowerCase() : "";
        int s = 0;
        if (name.startsWith(q)) s += 100; else if (name.contains(q)) s += 60;
        if (acc.startsWith(q)) s += 90; else if (acc.contains(q)) s += 50;
        if (ph.startsWith(q)) s += 80; else if (ph.contains(q)) s += 40;
        return s;
    }

    private void saveWithdraw() {
        String amountStr = etWithdrawAmount.getText().toString().trim();
        if (amountStr.isEmpty()) { Toast.makeText(this, "Please enter withdrawal amount", Toast.LENGTH_SHORT).show(); return; }
        if (selectedCustomer == null) { Toast.makeText(this, "Please select a customer", Toast.LENGTH_SHORT).show(); return; }

        double amt = Double.parseDouble(amountStr);
        if (amt <= 0) { Toast.makeText(this, "Amount must be greater than zero", Toast.LENGTH_SHORT).show(); return; }
        double negativeAmount = -Math.abs(amt);

        SimpleDateFormat dateSdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        SimpleDateFormat timeSdf = new SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault());
        Date now = new Date();
        String date = dateSdf.format(now);
        String time = timeSdf.format(now);

        String receiptNumber = generateReceiptNumber();
        String notes = etWithdrawNotes.getText() != null ? etWithdrawNotes.getText().toString().trim() : "";
        if (notes.isEmpty()) notes = "withdrawal";

        // Use Deposit model with negative amount, interest 0, totalAmount==amount for clarity
        Deposit withdrawal = new Deposit(
                selectedCustomer.getId(),
                selectedCustomer.getAccountNumber(),
                negativeAmount,
                date,
                time,
                0.0,
                negativeAmount,
                "",
                receiptNumber,
                notes,
                selectedCustomer.getName()
        );

        DatabaseReference customerTxRef = transactionsRef.child(selectedCustomer.getPhoneNumber()).push();
        customerTxRef.setValue(withdrawal)
                .addOnSuccessListener(unused -> Toast.makeText(WithdrawActivity.this, "Withdrawal saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(WithdrawActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        finish();
    }

    private String generateReceiptNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Constants.RECEIPT_PREFIX + "W" + sdf.format(new Date());
    }
}
