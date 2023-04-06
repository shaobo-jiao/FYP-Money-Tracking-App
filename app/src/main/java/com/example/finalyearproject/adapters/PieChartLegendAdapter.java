package com.example.finalyearproject.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.R;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.List;
import java.util.Locale;

public class PieChartLegendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<PieEntry> pieEntries; // info for legend items, size should <= 5;
    private int[] colors = ColorTemplate.JOYFUL_COLORS;

    public PieChartLegendAdapter(Context context, List<PieEntry> pieEntries) {
        this.context = context;
        this.pieEntries = pieEntries;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pie_chart_legend_item, parent, false);
        return new LegendItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        LegendItemHolder holder = (LegendItemHolder) viewHolder;
        PieEntry entry = pieEntries.get(position);
        TextViewCompat.setCompoundDrawableTintList(holder.tvCategoryName, ColorStateList.valueOf(colors[position]));
        holder.tvCategoryName.setText(entry.getLabel());
        holder.tvPercentage.setText(String.format(Locale.US, "%.2f%%", entry.getValue()));
    }

    @Override
    public int getItemCount() {
        return Math.min(5, pieEntries.size());
    }

    public static class LegendItemHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategoryName;
        private final TextView tvPercentage;

        public LegendItemHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
        }
    }
}
