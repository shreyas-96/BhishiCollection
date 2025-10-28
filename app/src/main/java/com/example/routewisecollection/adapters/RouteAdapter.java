package com.example.routewisecollection.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routewisecollection.R;
import com.example.routewisecollection.models.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private List<Route> routes = new ArrayList<>();
    private OnRouteClickListener listener;
    private OnRouteDeleteListener deleteListener;

    public interface OnRouteClickListener {
        void onRouteClick(Route route);
    }

    public interface OnRouteDeleteListener {
        void onRouteDelete(Route route);
    }

    public void setOnRouteClickListener(OnRouteClickListener listener) {
        this.listener = listener;
    }

    public void setOnRouteDeleteListener(OnRouteDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        holder.bind(routes.get(position));
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRouteName, tvRouteCode, tvCustomerCount;
        private Context context;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvRouteName = itemView.findViewById(R.id.tvRouteName);
            tvRouteCode = itemView.findViewById(R.id.tvRouteCode);
            tvCustomerCount = itemView.findViewById(R.id.tvCustomerCount);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRouteClick(routes.get(position));
                }
            });

            // Long click for delete
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    showDeleteDialog(routes.get(position));
                }
                return true;
            });
        }

        private void showDeleteDialog(Route route) {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Route")
                    .setMessage("Are you sure you want to delete route '" + route.getRouteName() + "'?\n\nThis action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (deleteListener != null) {
                            deleteListener.onRouteDelete(route);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        public void bind(Route route) {
            tvRouteName.setText(route.getRouteName());
            tvRouteCode.setText("Code: " + route.getRouteCode());
            tvCustomerCount.setText(route.getCustomerCount() + " Customers");
        }
    }
}
