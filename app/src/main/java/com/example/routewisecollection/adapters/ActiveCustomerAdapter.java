package com.example.routewisecollection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routewisecollection.R;
import com.example.routewisecollection.models.ActiveCustomer;

import java.util.List;
import java.util.Locale;

public class ActiveCustomerAdapter extends RecyclerView.Adapter<ActiveCustomerAdapter.ViewHolder> {

    private final List<ActiveCustomer> customers;
    private final Context context;

    public ActiveCustomerAdapter(Context context, List<ActiveCustomer> customers) {
        this.context = context;
        this.customers = customers;
    }

    @NonNull
    @Override
    public ActiveCustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_active_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveCustomerAdapter.ViewHolder holder, int position) {
        ActiveCustomer customer = customers.get(position);
        
        // Set customer initial (first letter of name)
        String initial = customer.getName() != null && !customer.getName().isEmpty() 
                ? customer.getName().substring(0, 1).toUpperCase() 
                : "?";
        holder.tvInitial.setText(initial);
        
        holder.tvName.setText(customer.getName());
        holder.tvAccount.setText(customer.getAccountNumber());
        holder.tvPhone.setText(customer.getPhoneNumber());
        holder.tvPrincipal.setText("₹" + String.format(Locale.getDefault(), "%.2f", customer.getPrincipalAmount()));
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAccount, tvPhone, tvPrincipal, tvInitial;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCustomerName);
            tvAccount = itemView.findViewById(R.id.tvAccountNumber);
            tvPhone = itemView.findViewById(R.id.tvPhoneNumber);
            tvPrincipal = itemView.findViewById(R.id.tvPrincipalAmount);
            tvInitial = itemView.findViewById(R.id.tvCustomerInitial);
        }
    }
}
