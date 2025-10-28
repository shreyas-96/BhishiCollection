package com.example.routewisecollection.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routewisecollection.R;
import com.example.routewisecollection.models.Customer;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customers = new ArrayList<>();
    private List<Customer> customersFiltered = new ArrayList<>();
    private OnCustomerActionListener listener;
    private Context context;

    public interface OnCustomerActionListener {
        void onEditCustomer(Customer customer);
        void onDeleteCustomer(Customer customer);
        void onCustomerClick(Customer customer);
        String getRouteName(int routeId);
    }

    public CustomerAdapter(Context context) {
        this.context = context;
    }

    public void setOnCustomerActionListener(OnCustomerActionListener listener) {
        this.listener = listener;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
        this.customersFiltered = new ArrayList<>(customers);
        notifyDataSetChanged();
    }

    public void filterByRoute(int routeId) {
        customersFiltered.clear();
        if (routeId == -1) {
            // Show all customers
            customersFiltered.addAll(customers);
        } else {
            // Filter by specific route
            for (Customer customer : customers) {
                if (customer.getRouteId() == routeId) {
                    customersFiltered.add(customer);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterBySearch(String query) {
        customersFiltered.clear();
        if (query.isEmpty()) {
            customersFiltered.addAll(customers);
        } else {
            String searchQuery = query.toLowerCase().trim();
            for (Customer customer : customers) {
                if (customer.getName().toLowerCase().contains(searchQuery) ||
                    customer.getAccountNumber().toLowerCase().contains(searchQuery) ||
                    customer.getPhoneNumber().contains(searchQuery)) {
                    customersFiltered.add(customer);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        holder.bind(customersFiltered.get(position));
    }

    @Override
    public int getItemCount() {
        return customersFiltered.size();
    }

    class CustomerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCustomerName, tvAccountNumber, tvPhoneNumber, tvRouteName;
        private TextView tvPrincipalAmount, tvInterestRate, tvCreatedDateTime, tvUpdatedDateTime;
        private TextView tvCustomerInitial;
        private Button btnEditCustomer, btnDeleteCustomer;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvAccountNumber = itemView.findViewById(R.id.tvAccountNumber);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvRouteName = itemView.findViewById(R.id.tvRouteName);
            tvPrincipalAmount = itemView.findViewById(R.id.tvPrincipalAmount);
            tvInterestRate = itemView.findViewById(R.id.tvInterestRate);
            tvCreatedDateTime = itemView.findViewById(R.id.tvCreatedDateTime);
            tvUpdatedDateTime = itemView.findViewById(R.id.tvUpdatedDateTime);
            tvCustomerInitial = itemView.findViewById(R.id.tvCustomerInitial);
            btnEditCustomer = itemView.findViewById(R.id.btnEditCustomer);
            btnDeleteCustomer = itemView.findViewById(R.id.btnDeleteCustomer);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCustomerClick(customersFiltered.get(position));
                }
            });

            btnEditCustomer.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditCustomer(customersFiltered.get(position));
                }
            });

            btnDeleteCustomer.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    showDeleteDialog(customersFiltered.get(position));
                }
            });
        }

        private void showDeleteDialog(Customer customer) {
            new AlertDialog.Builder(context)
                    .setTitle("🗑️ Delete Customer")
                    .setMessage("Are you sure you want to delete customer '" + customer.getName() + "'?\n\nThis will also delete all associated deposits and cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (listener != null) {
                            listener.onDeleteCustomer(customer);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        public void bind(Customer customer) {
            // Set customer initial (first letter of name)
            String initial = customer.getName() != null && !customer.getName().isEmpty() 
                    ? customer.getName().substring(0, 1).toUpperCase() 
                    : "?";
            tvCustomerInitial.setText(initial);
            
            tvCustomerName.setText(customer.getName());
            tvAccountNumber.setText("Account: " + customer.getAccountNumber());
            tvPhoneNumber.setText(customer.getPhoneNumber());
            
            // Get route name from listener
            if (listener != null) {
                tvRouteName.setText(listener.getRouteName(customer.getRouteId()));
            }

            // Format principal amount
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            tvPrincipalAmount.setText(currencyFormat.format(customer.getPrincipalAmount()));
            
            // Format interest rate
            tvInterestRate.setText(String.format("%.1f%%", customer.getInterestRate()));

            // Format created date and time
            if (customer.getCreatedDate() != null && customer.getCreatedTime() != null) {
                tvCreatedDateTime.setText(customer.getCreatedDate() + " at " + customer.getCreatedTime());
            } else {
                tvCreatedDateTime.setText("Date not available");
            }

            // Show updated date if available
            if (customer.getUpdatedDate() != null && !customer.getUpdatedDate().isEmpty()) {
                tvUpdatedDateTime.setText("Updated: " + customer.getUpdatedDate());
                tvUpdatedDateTime.setVisibility(View.VISIBLE);
            } else {
                tvUpdatedDateTime.setVisibility(View.GONE);
            }
        }
    }
}
