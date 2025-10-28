package com.example.routewisecollection.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routewisecollection.R;
import com.example.routewisecollection.adapters.RouteAdapter;
import com.example.routewisecollection.models.Route;
import com.example.routewisecollection.viewmodel.DepositViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.routewisecollection.utils.LoginManager;

import java.util.ArrayList;
import java.util.List;

public class RouteListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RouteAdapter routeAdapter;
    private DepositViewModel viewModel;
    private FloatingActionButton fabAddRoute;
    private String assignedRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);

        initializeViews();
        // Read assigned route for logged-in agent from session
        LoginManager loginManager = new LoginManager(this);
        assignedRoute = loginManager.getRoute();
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();
        addDefaultRoutes();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewRoutes);
        fabAddRoute = findViewById(R.id.fabAddRoute);
    }

    private void setupRecyclerView() {
        routeAdapter = new RouteAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(routeAdapter);

        // Set delete listener
        routeAdapter.setOnRouteDeleteListener(route -> {
            viewModel.deleteRoute(route);
            Toast.makeText(this, "Route deleted: " + route.getRouteName(), Toast.LENGTH_SHORT).show();
        });

        // Set click listener
        routeAdapter.setOnRouteClickListener(route -> {
            Toast.makeText(this, "Selected: " + route.getRouteName(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DepositViewModel.class);
        
        viewModel.getAllActiveRoutes().observe(this, routes -> {
            if (routes != null) {
                if (assignedRoute != null && !assignedRoute.trim().isEmpty()) {
                    List<Route> filtered = new ArrayList<>();
                    String ar = assignedRoute.trim();
                    for (Route r : routes) {
                        if (r == null) continue;
                        String name = r.getRouteName() != null ? r.getRouteName() : "";
                        String code = r.getRouteCode() != null ? r.getRouteCode() : "";
                        if (name.equalsIgnoreCase(ar) || code.equalsIgnoreCase(ar)) {
                            filtered.add(r);
                        }
                    }
                    routeAdapter.setRoutes(filtered);
                    // Hide add button for agents restricted to a route
                    if (fabAddRoute != null) {
                        fabAddRoute.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                } else {
                    // If no assignment, show all (likely admin)
                    routeAdapter.setRoutes(routes);
                }
            }
        });
    }

    private void setupClickListeners() {
        fabAddRoute.setOnClickListener(v -> showAddRouteDialog());
    }

    private void addDefaultRoutes() {
        // Add default routes for Sangli, Kolhapur, Satara
        new Thread(() -> {
            // Check if routes already exist
            if (viewModel.getRouteCount() == 0) {
                Route sangli = new Route("Sangli", "SNG", 0);
                Route kolhapur = new Route("Kolhapur", "KLP", 0);
                Route satara = new Route("Satara", "STR", 0);

                viewModel.insertRoute(sangli);
                viewModel.insertRoute(kolhapur);
                viewModel.insertRoute(satara);
            }
        }).start();
    }

    private void showAddRouteDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_route, null);
        EditText etRouteName = dialogView.findViewById(R.id.etRouteName);
        EditText etRouteCode = dialogView.findViewById(R.id.etRouteCode);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnSave.setOnClickListener(v -> {
            String routeName = etRouteName.getText().toString().trim();
            String routeCode = etRouteCode.getText().toString().trim().toUpperCase();

            if (routeName.isEmpty()) {
                etRouteName.setError("Route name is required");
                return;
            }

            if (routeCode.isEmpty()) {
                etRouteCode.setError("Route code is required");
                return;
            }

            Route newRoute = new Route(routeName, routeCode, 0);
            viewModel.insertRoute(newRoute);
            
            Toast.makeText(this, "Route added: " + routeName, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
