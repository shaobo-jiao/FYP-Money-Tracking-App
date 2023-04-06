package com.example.finalyearproject.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.R;
import com.example.finalyearproject.beans.Record;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PieChartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private double totalAmount;
    private int recordType;
    private final List<PieEntry> pieEntries;

    private boolean isEmptyCategoryList;
    private final int[] colors = ColorTemplate.JOYFUL_COLORS;

    public PieChartAdapter(Context context, double totalAmount, int recordType, List<PieEntry> pieEntries, boolean isEmptyCategoryList) {
        this.context = context;
        this.totalAmount = totalAmount;
        this.recordType = recordType;
        this.pieEntries = pieEntries;
        this.isEmptyCategoryList = isEmptyCategoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pie_chart, parent, false);
        return new PieChartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Log.d("FYP_onBindViewHolder", "called");
        PieChartHolder holder = (PieChartHolder) viewHolder;
        // draw pieChart;
        drawPieChart(holder.pieChart);
        // setup legends;
        drawLegend(holder.rvPieChartLegend);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    /* configure and draw pieChart, set data */
    private void drawPieChart(PieChart pieChart) {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(80);
        pieChart.setTransparentCircleRadius(0);
        pieChart.setDrawEntryLabels(false);
        String type = (recordType == Record.RECORD_EXPENSE) ? "Expense" : "Income";
        SpannableString centerText = new SpannableString(String.format(Locale.US,"%s\n%.2f", type, totalAmount));
        // use spannableString to set record type a smaller size and grey_text;
        centerText.setSpan(new RelativeSizeSpan(0.7f),
                0, type.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        centerText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey_text)),
                0, type.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pieChart.setCenterText(centerText);
        pieChart.setCenterTextSize(18);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        PieDataSet dataSet;
        if (!isEmptyCategoryList) {
            dataSet = new PieDataSet(pieEntries, "");
            dataSet.setColors(colors);
        }
        else {
            // for empty data, overwrite with dummy pieEntries and colors to draw grey pieChart;
            List<PieEntry> dummy = new ArrayList<>();
            dummy.add(new PieEntry(1, ""));
            dataSet = new PieDataSet(dummy, "");
            int[] greyColors = new int[] {ContextCompat.getColor(context, R.color.grey_icon_bg)};
            dataSet.setColors(greyColors);
        }
        dataSet.setDrawValues(false);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(1000, Easing.EaseOutQuad);
    }

    /* draw legends */
    private void drawLegend(RecyclerView rvLegend) {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rvLegend.setLayoutManager(manager);
        PieChartLegendAdapter legendAdapter = new PieChartLegendAdapter(context, pieEntries);
        rvLegend.setAdapter(legendAdapter);
    }

    public void setEmptyCategoryList(boolean emptyCategoryList) {
        isEmptyCategoryList = emptyCategoryList;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public static class PieChartHolder extends RecyclerView.ViewHolder {
        private final PieChart pieChart;
        private final RecyclerView rvPieChartLegend;

        public PieChartHolder(@NonNull View itemView) {
            super(itemView);
            pieChart = itemView.findViewById(R.id.pie_chart);
            rvPieChartLegend = itemView.findViewById(R.id.rv_pie_chart_legend);
        }
    }
}
