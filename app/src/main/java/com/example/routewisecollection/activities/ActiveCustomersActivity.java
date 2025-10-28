package com.example.routewisecollection.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routewisecollection.R;
import com.example.routewisecollection.adapters.ActiveCustomerAdapter;
import com.example.routewisecollection.models.ActiveCustomer;
import com.example.routewisecollection.utils.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActiveCustomersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ActiveCustomerAdapter adapter;
    private List<ActiveCustomer> customerList = new ArrayList<>();

    private DatabaseReference customersRef;
    private String agentMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        recyclerView = findViewById(R.id.recyclerViewActiveCustomers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActiveCustomerAdapter(this, customerList);
        recyclerView.setAdapter(adapter);

        // Get agent mobile from session
        LoginManager loginManager = new LoginManager(this);
        agentMobile = loginManager.getAgentMobile();

        // Initialize Firebase reference
        customersRef = FirebaseDatabase.getInstance()
                .getReference("agents")
                .child(agentMobile)
                .child("customers");

        loadActiveCustomers();
    }

    private void loadActiveCustomers() {
        customersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                customerList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ActiveCustomer customer = ds.getValue(ActiveCustomer.class);
                    if (customer != null) {
                        customerList.add(customer);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActiveCustomersActivity.this, "Failed to load customers", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
