package com.example.finalyearproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.R;
import com.example.finalyearproject.beans.CategoryAggData;
import com.example.finalyearproject.beans.Record;

import java.util.List;
import java.util.Locale;

public class ChartRecordListItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int recordType;
    private List<CategoryAggData> categoryAggDataList;

    public ChartRecordListItemAdapter(Context context, int recordType, List<CategoryAggData> categoryAggDataList) {
        this.context = context;
        this.recordType = recordType;
        this.categoryAggDataList = categoryAggDataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chart_record_list_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ItemHolder holder = (ItemHolder) viewHolder;
        CategoryAggData data = categoryAggDataList.get(position);
        holder.ivCategoryIcon.setImageResource(data.getImageId());
        holder.ivCategoryIcon.setImageTintList(context.getColorStateList(R.color.white));
        // set image icon bg color: diff color for expense and income
        if (recordType == Record.RECORD_EXPENSE)
            holder.ivCategoryIcon.setBackgroundTintList(context.getColorStateList(R.color.red));
        else
            holder.ivCategoryIcon.setBackgroundTintList(context.getColorStateList(R.color.green));
        holder.tvCategoryName.setText(data.getName());
        holder.tvPercentage.setText(String.format(Locale.US, "%.2f%%", data.getPercentage()));
        holder.tvAmount.setText(String.format(Locale.US, "%.2f", data.getTotal()));
    }

    @Override
    public int getItemCount() {
        return categoryAggDataList.size();
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCategoryIcon;
        private final TextView tvCategoryName;
        private final TextView tvPercentage;
        private final TextView tvAmount;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }
    }

}
