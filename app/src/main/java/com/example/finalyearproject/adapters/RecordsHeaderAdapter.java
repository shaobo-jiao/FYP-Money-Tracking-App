package com.example.finalyearproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.R;
import com.example.finalyearproject.beans.RecordsHeader;

import java.util.Locale;

public class RecordsHeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private RecordsHeader header;

    private View.OnClickListener onBudgetClickListener;

    public RecordsHeaderAdapter(Context context, RecordsHeader header) {
        this.context = context;
        this.header = header;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.records_list_header, parent, false);
        return new RecordsHeaderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        RecordsHeaderHolder holder = (RecordsHeaderHolder) viewHolder;
        holder.tvBudgetSurplus.setText(String.format(Locale.US, "%.2f", header.getBudgetSurplus()));
        holder.tvTotalExpense.setText(String.format(Locale.US, "%.2f", header.getTotalExpense()));
        holder.tvTotalIncome.setText(String.format(Locale.US, "%.2f", header.getTotalIncome()));
        holder.llBudgetSurplus.setOnClickListener(v -> onBudgetClickListener.onClick(v));
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    public void setOnBudgetClickListener(View.OnClickListener onBudgetClickListener) {
        this.onBudgetClickListener = onBudgetClickListener;
    }

    public static class RecordsHeaderHolder extends RecyclerView.ViewHolder {
        private final LinearLayout llBudgetSurplus;
        private final TextView tvBudgetSurplus;
        private final TextView tvTotalExpense;
        private final TextView tvTotalIncome;


        public RecordsHeaderHolder(@NonNull View itemView) {
            super(itemView);
            llBudgetSurplus = itemView.findViewById(R.id.ll_budget_surplus);
            tvBudgetSurplus = itemView.findViewById(R.id.tv_budget_surplus);
            tvTotalExpense = itemView.findViewById(R.id.tv_total_expense);
            tvTotalIncome = itemView.findViewById(R.id.tv_total_income);
        }

    }

}
