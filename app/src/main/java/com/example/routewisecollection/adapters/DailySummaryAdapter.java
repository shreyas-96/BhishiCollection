package com.example.routewisecollection.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routewisecollection.R;
import com.example.routewisecollection.models.ReportData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailySummaryAdapter extends RecyclerView.Adapter<DailySummaryAdapter.ViewHolder> {

    private List<ReportData.DailySummary> summaries;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());

    public DailySummaryAdapter(List<ReportData.DailySummary> summaries) {
        this.summaries = summaries;
    }

    public DailySummaryAdapter() {
        
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportData.DailySummary summary = summaries.get(position);
        holder.bind(summary);
    }

    @Override
    public int getItemCount() {
        return summaries.size();
    }

    public void updateSummaries(List<ReportData.DailySummary> newSummaries) {
        this.summaries = newSummaries;
        notifyDataSetChanged();
    }

    public void setDailySummaries(List<ReportData.DailySummary> dailySummaries) {
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvDate, tvAmount, tvEntries;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardDailySummary);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvEntries = itemView.findViewById(R.id.tvEntries);
        }

        public void bind(ReportData.DailySummary summary) {
            // Format date for display
            try {
                Date date = dateFormat.parse(summary.getDate());
                tvDate.setText(displayDateFormat.format(date));
            } catch (Exception e) {
                tvDate.setText(summary.getDate());
            }
            
            tvAmount.setText("₹" + String.format(Locale.getDefault(), "%.2f", summary.getTotalAmount()));
            tvEntries.setText(summary.getEntryCount() + " entries");

            // Color coding based on amount
            if (summary.getTotalAmount() >= 5000) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.success_light));
            } else if (summary.getTotalAmount() >= 2000) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.warning));
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.card_background));
            }
        }
    }
}
