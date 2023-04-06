package com.example.finalyearproject.fragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalyearproject.MainApplication;
import com.example.finalyearproject.R;
import com.example.finalyearproject.adapters.ChartRecordListAdapter;
import com.example.finalyearproject.adapters.PieChartAdapter;
import com.example.finalyearproject.beans.CategoryAggData;
import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.dao.RecordDao;
import com.example.finalyearproject.databinding.FragmentChartsBinding;
import com.example.finalyearproject.dialogs.MonthPickerDialog;
import com.example.finalyearproject.utils.DateUtils;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ChartsFragment extends Fragment {
    private static final String TAG = "ChartsFragment";
    private com.example.finalyearproject.databinding.FragmentChartsBinding binding;
    private int year, month; // keep track of year, month of shown statistics;
    private int recordType = Record.RECORD_EXPENSE; // keep track of recordType of shown statistics;
    private List<CategoryAggData> categoryAggDataList = new ArrayList<>();
    private final List<PieEntry> pieEntries = new ArrayList<>();
    private PieChartAdapter pieChartAdapter;
    private ChartRecordListAdapter listAdapter;

    public ChartsFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategoryAggData(year, month, recordType);
        refreshPieChart();
        refreshList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChartsBinding.inflate(inflater, container, false);

        initTvMonth();
        initRvCategoryStatistics();
        initBtns();

        return binding.getRoot();
    }



    /* setup initial year, month, register onClickListener on tvMonth */
    private void initTvMonth() {
        // set current year-month as initial year-month;
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        binding.tvMonth.setText(DateUtils.getYearMonthStr(year, month));
        binding.tvMonth.setOnClickListener(v -> {
            // show month picker dialog
            MonthPickerDialog dialog = new MonthPickerDialog(getContext(), year, month);
            dialog.setOnMonthSetListener((year, month) -> {
                // onMonthSet: update year, month; load new categoryAggData, notify change
                this.year = year;
                this.month = month;
                binding.tvMonth.setText(DateUtils.getYearMonthStr(year, month));
                loadCategoryAggData(year, month, recordType);
                refreshPieChart();
                refreshList();
            });
            dialog.show();
            dialog.setDialogPosition();
        });
    }

    /* set layout and adapters for rvCategoryStatistics */
    private void initRvCategoryStatistics() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.rvCategoryStatistics.setLayoutManager(manager);
        pieChartAdapter = new PieChartAdapter(getContext(), computeTotalAmount(), recordType, pieEntries, categoryAggDataList.isEmpty());
        listAdapter = new ChartRecordListAdapter(getContext(), recordType, categoryAggDataList);
        binding.rvCategoryStatistics.setAdapter(new ConcatAdapter(pieChartAdapter, listAdapter));
    }

    /* set onClickListener for "Expense" and "Income" buttons */
    private void initBtns() {
        refreshBtns();
        binding.tvExpenseBtn.setOnClickListener(v -> {
            if (recordType != Record.RECORD_EXPENSE) {
                // switch from Income to Expense
                recordType = Record.RECORD_EXPENSE;
                refreshBtns();
                loadCategoryAggData(year, month, recordType);
                refreshPieChart();
                refreshList();
            }
        });
        binding.tvIncomeBtn.setOnClickListener(v -> {
            if (recordType != Record.RECORD_INCOME) {
                // switch from Expense to Income
                recordType = Record.RECORD_INCOME;
                refreshBtns();
                loadCategoryAggData(year, month, recordType);
                refreshPieChart();
                refreshList();
            }
        });
    }

    /* refresh "Expense" and "Income" buttons' bg_color and text_color depending on new recordType */
    private void refreshBtns() {
        if (recordType == Record.RECORD_EXPENSE) {
            binding.tvExpenseBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            binding.tvExpenseBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.blue));
            binding.tvIncomeBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            binding.tvIncomeBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
        }
        else {
            binding.tvIncomeBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            binding.tvIncomeBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.blue));
            binding.tvExpenseBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            binding.tvExpenseBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
        }
    }

    /* when new categoryAggDataList is loaded, refresh PieChart */
    private void refreshPieChart() {
        preparePieEntries();
        pieChartAdapter.setRecordType(recordType);
        pieChartAdapter.setEmptyCategoryList(categoryAggDataList.isEmpty());
        pieChartAdapter.setTotalAmount(computeTotalAmount());
        pieChartAdapter.notifyDataSetChanged();
    }

    /* when new categoryAggDataList is loaded, refresh List */
    private void refreshList() {
        listAdapter.setRecordType(recordType);
        listAdapter.notifyDataSetChanged();
    }

    /* use loaded CategoryAggData to prepare <= 5 PieEntries */
    private void preparePieEntries() {
        pieEntries.clear();
        Log.d(TAG, "preparePieEntries: " + categoryAggDataList.size());
        // if no category data, return dummy entries;
        if (categoryAggDataList.size() == 0) {
            for (int i = 0; i < 5; i++) {
                pieEntries.add(new PieEntry(0, "---"));
            }
        }
        // if nCategories <= 5: use all to create PieEntries;
        else if (categoryAggDataList.size() <= 5){
            for (CategoryAggData data: categoryAggDataList) {
                pieEntries.add(new PieEntry((float) data.getPercentage(), data.getName()));
            }
        }
        // if nCategory >= 5: draw first 4 categories with biggest amount, and Other as 5th
        else {
            float percentage = 0;
            for (int i = 0; i < 4; i++) {
                CategoryAggData data = categoryAggDataList.get(i);
                pieEntries.add(new PieEntry((float) data.getPercentage(), data.getName()));
                percentage += (float) data.getPercentage();
            }
            // Other as 5th with remaining percentage
            pieEntries.add(new PieEntry(100 - percentage, "Other"));
        }
    }

    /* compute total amount of categoryAggData */
    private double computeTotalAmount() {
        double total = 0;
        for (CategoryAggData data: categoryAggDataList) {
            total += data.getTotal();
        }
        return total;
    }

    private void loadCategoryAggData(int year, int month, int recordType) {
        categoryAggDataList.clear();
        RecordDao recordDao = MainApplication.getInstance().getMainDatabase().recordDao();
        categoryAggDataList.addAll(recordDao.getCategoryAggData(year,month, recordType));
        Log.d(TAG, "loadCategoryAggData: " + categoryAggDataList.size());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}