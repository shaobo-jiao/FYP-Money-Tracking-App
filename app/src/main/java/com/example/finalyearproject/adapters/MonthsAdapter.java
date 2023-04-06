package com.example.finalyearproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.R;
import com.example.finalyearproject.utils.DateUtils;

import java.util.List;

public class MonthsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private int selectedPos = -1; // month selected
    private OnItemClickListener onItemClickListener;

    public MonthsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.month_button, parent, false);
        return new MonthBtnHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MonthBtnHolder holder = (MonthBtnHolder) viewHolder;
        holder.tvMonth.setText(months[position]);
        // for the month being selected, change diff background color
        if (position != selectedPos) {
            holder.tvMonth.setTextColor(context.getColorStateList(R.color.grey_text));
            holder.tvMonth.setBackgroundTintList(context.getColorStateList(R.color.white));
        }
        else {
            holder.tvMonth.setTextColor(context.getColorStateList(R.color.white));
            holder.tvMonth.setBackgroundTintList(context.getColorStateList(R.color.blue));
        }
        holder.itemView.setOnClickListener(v -> {
            onItemClickListener.onItemClick(v, position);
        });
    }

    @Override
    public int getItemCount() {
        return months.length;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }

    public static class MonthBtnHolder extends RecyclerView.ViewHolder {
        private final TextView tvMonth;

        public MonthBtnHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tv_month);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
