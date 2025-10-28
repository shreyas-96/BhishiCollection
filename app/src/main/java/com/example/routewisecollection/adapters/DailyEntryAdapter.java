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

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DailyEntryAdapter extends RecyclerView.Adapter<DailyEntryAdapter.DailyEntryViewHolder> {

    private List<Deposit> deposits = Collections.emptyList();
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Deposit deposit, int position);
    }

    public DailyEntryAdapter(List<Deposit> deposits) {
        if (deposits != null) this.deposits = deposits;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void updateDeposits(List<Deposit> deposits) {
        this.deposits = deposits != null ? deposits : Collections.emptyList();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DailyEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_entry, parent, false);
        return new DailyEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyEntryViewHolder holder, int position) {
        Deposit deposit = deposits.get(position);

        // Set the customer's account number here properly
        holder.tvCustomerId.setText("Account Number: " + deposit.getAccountNumber());

        holder.tvTime.setText(deposit.getDepositTime());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "₹%.2f", deposit.getAmount()));

        String notes = deposit.getNotes();
        if (notes != null && !notes.isEmpty()) {
            holder.tvRemarks.setVisibility(View.VISIBLE);
            holder.tvRemarks.setText("Remarks: " + notes);
        } else {
            holder.tvRemarks.setVisibility(View.GONE);
        }

        // Handle delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(deposit, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deposits != null ? deposits.size() : 0;
    }

    public static class DailyEntryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerId, tvTime, tvAmount, tvRemarks;
        ImageButton btnDelete;

        public DailyEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerId = itemView.findViewById(R.id.tvCustomerId);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
