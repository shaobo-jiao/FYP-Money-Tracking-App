package com.example.finalyearproject.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.MainApplication;
import com.example.finalyearproject.R;
import com.example.finalyearproject.RecordDetailsActivity;
import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.dao.RecordDao;

import java.util.List;
import java.util.Locale;

public class DailyRecordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Record> recordList;

    public DailyRecordsAdapter(Context context, List<Record> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.record, parent, false);
        return new RecordHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        RecordHolder holder = (RecordHolder) viewHolder;
        Record record = recordList.get(position);
        holder.ivCategoryIcon.setImageResource(record.getCategoryImageId());
        holder.ivCategoryIcon.setImageTintList(context.getColorStateList(R.color.white));
        // set image icon bg color: diff color for expense and income
        if (record.getRecordType() == Record.RECORD_EXPENSE) {
            holder.ivCategoryIcon.setBackgroundTintList(context.getColorStateList(R.color.red));
        } else {
            holder.ivCategoryIcon.setBackgroundTintList(context.getColorStateList(R.color.green));
        }
        holder.tvCategoryName.setText(record.getCategoryName());
        holder.tvDesc.setText(record.getDesc());
        // negative amount for expenses
        double amount = record.getAmount();
        if (record.getRecordType() == Record.RECORD_EXPENSE)
            amount = -amount;
        holder.tvAmount.setText(String.format(Locale.US, "%.2f", amount));
        holder.itemView.setOnClickListener(v -> {
            // on record click, start RecordDetailsActivity
            Intent intent = new Intent(context, RecordDetailsActivity.class);
            intent.putExtra("recordId", record.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class RecordHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCategoryIcon;
        private final TextView tvCategoryName;
        private final TextView tvDesc;
        private final TextView tvAmount;

        public RecordHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }
    }

}
