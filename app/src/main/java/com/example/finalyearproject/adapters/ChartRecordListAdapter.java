package com.example.finalyearproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.R;
import com.example.finalyearproject.beans.CategoryAggData;
import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.components.SimpleDividerItemDecoration;

import java.util.List;

public class ChartRecordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private int recordType;
    private List<CategoryAggData> categoryAggDataList;

    public ChartRecordListAdapter(Context context, int recordType, List<CategoryAggData> categoryAggDataList) {
        this.context = context;
        this.recordType = recordType;
        this.categoryAggDataList = categoryAggDataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chart_record_list, parent, false);
        return new ListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ListHolder holder = (ListHolder) viewHolder;
        holder.tvListName.setText((recordType == Record.RECORD_EXPENSE) ? "Expense List" : "Income List");
        // setup rvRecordList: adapter and layout;
        LinearLayoutManager manager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        holder.rvRecordList.setLayoutManager(manager);
        ChartRecordListItemAdapter adapter = new ChartRecordListItemAdapter(context, recordType, categoryAggDataList);
        holder.rvRecordList.setAdapter(adapter);
        holder.rvRecordList.addItemDecoration(new SimpleDividerItemDecoration(context));
    }

    @Override
    public int getItemCount() {
        return categoryAggDataList.isEmpty() ? 0 : 1;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public static class ListHolder extends RecyclerView.ViewHolder {

        private final TextView tvListName;
        private final RecyclerView rvRecordList;

        public ListHolder(@NonNull View itemView) {
            super(itemView);
            tvListName = itemView.findViewById(R.id.tv_list_name);
            rvRecordList = itemView.findViewById(R.id.rv_record_list);
        }
    }


}
