package com.example.finalyearproject.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.R;
import com.example.finalyearproject.beans.DailyRecordsCard;
import com.example.finalyearproject.components.SimpleDividerItemDecoration;

import java.util.List;
import java.util.Locale;

public class MonthlyRecordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<DailyRecordsCard> dailyRecordsCardList;

    public MonthlyRecordsAdapter(Context context, List<DailyRecordsCard> dailyRecordsCardList) {
        this.context = context;
        this.dailyRecordsCardList = dailyRecordsCardList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.daily_records_card, parent, false);
        return new DailyRecordsCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        DailyRecordsCardHolder holder = (DailyRecordsCardHolder) viewHolder;
        DailyRecordsCard card = dailyRecordsCardList.get(position);
        holder.tvDate.setText(card.getDate());
        holder.tvTotalExpense.setText(String.format(Locale.US, "%.2f", card.getTotalExpense()));
        holder.tvTotalIncome.setText(String.format(Locale.US, "%.2f", card.getTotalIncome()));
        // set layout and adapter for each day's record list;
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.rvDailyRecords.setLayoutManager(manager);
        DailyRecordsAdapter adapter = new DailyRecordsAdapter(context, card.getRecordList());
        holder.rvDailyRecords.setAdapter(adapter);
        // add line decoration;
        holder.rvDailyRecords.addItemDecoration(new SimpleDividerItemDecoration(context));
    }

    @Override
    public int getItemCount() {
        return dailyRecordsCardList.size();
    }

    public static class DailyRecordsCardHolder extends RecyclerView.ViewHolder {
        private final TextView tvDate;
        private final TextView tvTotalExpense;
        private final TextView tvTotalIncome;
        private final RecyclerView rvDailyRecords;

        public DailyRecordsCardHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTotalExpense = itemView.findViewById(R.id.tv_total_expense);
            tvTotalIncome = itemView.findViewById(R.id.tv_total_income);
            rvDailyRecords = itemView.findViewById(R.id.rv_daily_records);

        }
    }

}
