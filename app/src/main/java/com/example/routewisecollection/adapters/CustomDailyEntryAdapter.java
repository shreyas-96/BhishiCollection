package com.example.routewisecollection.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routewisecollection.R;
import com.example.routewisecollection.models.Deposit;

import java.util.List;
import java.util.Locale;

public class CustomDailyEntryAdapter extends RecyclerView.Adapter<CustomDailyEntryAdapter.EntryViewHolder> {

    private List<Deposit> depositList;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Deposit deposit, int position);
    }

    public CustomDailyEntryAdapter(List<Deposit> depositList) {
        this.depositList = depositList;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void updateDeposits(List<Deposit> deposits) {
        this.depositList = deposits;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daily_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        Deposit deposit = depositList.get(position);
        // Display account number instead of customer name
        String accountInfo = deposit.getAccountNumber() != null ? 
                "Account Number: " + deposit.getAccountNumber() : 
                (deposit.getCustomerName() != null ? deposit.getCustomerName() : "Unknown");
        holder.tvCustomerId.setText(accountInfo);
        holder.tvDepositAmount.setText(String.format(Locale.getDefault(), "₹%.2f", deposit.getAmount()));
        holder.tvDepositTime.setText(deposit.getDepositTime() != null ? deposit.getDepositTime() : "-");

        // Handle delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(deposit, position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return depositList != null ? depositList.size() : 0;
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerId, tvDepositAmount, tvDepositTime;
        ImageButton btnDelete;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerId = itemView.findViewById(R.id.tvCustomerId);
            tvDepositAmount = itemView.findViewById(R.id.tvAmount);
            tvDepositTime = itemView.findViewById(R.id.tvTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
