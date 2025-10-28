package com.example.routewisecollection.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.routewisecollection.R;
import com.example.routewisecollection.models.Customer;
import com.example.routewisecollection.models.Route;
import com.example.routewisecollection.utils.LoginManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddCustomerActivity extends AppCompatActivity {

    private EditText etName, etPhone, etAddress, etPrincipal, etInterestRate, etAccountNumber;
    private Spinner spinnerRoute;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;

    private List<Route> routeList = new ArrayList<>();
    private DatabaseReference customersRef;
    private String agentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        // Initialize views
        etName = findViewById(R.id.etCustomerName);
        etPhone = findViewById(R.id.etCustomerPhone);
        etAddress = findViewById(R.id.etCustomerAddress);
        etPrincipal = findViewById(R.id.etPrincipalAmount);
        etInterestRate = findViewById(R.id.etInterestRate);
        etAccountNumber = findViewById(R.id.etAccountNumber);
        spinnerRoute = findViewById(R.id.spinnerRoute);
        btnSave = findViewById(R.id.btnSaveCustomer);
        btnCancel = findViewById(R.id.btnCancelCustomer);
        progressBar = findViewById(R.id.progressBar);

        // Get current agent
        LoginManager loginManager = new LoginManager(this);
        agentId = loginManager.getAgentMobile();

        customersRef = FirebaseDatabase.getInstance()
                .getReference("agents")
                .child(agentId)
                .child("customers");

        loadRoutes();
        setupClickListeners();
    }

    private void loadRoutes() {
        // Example routes — replace with actual data if needed
        routeList.add(new Route(1, "Sangli"));
        routeList.add(new Route(2, "Kolhapur"));
        routeList.add(new Route(3, "Satara"));

        List<String> routeNames = new ArrayList<>();
        for (Route route : routeList) routeNames.add(route.getRouteName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, routeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoute.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveCustomer());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveCustomer() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String principalStr = etPrincipal.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || principalStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        double principal = Double.parseDouble(principalStr);
        double interestRate = etInterestRate.getText().toString().isEmpty() ?
                2.0 : Double.parseDouble(etInterestRate.getText().toString());
        int routeId = routeList.get(spinnerRoute.getSelectedItemPosition()).getId();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Customer customer = new Customer(name, phone, address, routeId, principal, interestRate, date, etAccountNumber.getText().toString());

        // Save directly to Firebase using phone number as key
        customersRef.child(phone).setValue(customer)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Customer added under agent!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
