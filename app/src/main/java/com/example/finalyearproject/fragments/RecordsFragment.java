package com.example.finalyearproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalyearproject.MainApplication;
import com.example.finalyearproject.NewRecordActivity;
import com.example.finalyearproject.adapters.MonthlyRecordsAdapter;
import com.example.finalyearproject.adapters.RecordsHeaderAdapter;
import com.example.finalyearproject.beans.DailyRecordsCard;
import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.beans.RecordsHeader;
import com.example.finalyearproject.dao.RecordDao;
import com.example.finalyearproject.databinding.FragmentRecordsBinding;
import com.example.finalyearproject.dialogs.BudgetSettingDialog;
import com.example.finalyearproject.dialogs.MonthPickerDialog;
import com.example.finalyearproject.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class RecordsFragment extends Fragment {
    private com.example.finalyearproject.databinding.FragmentRecordsBinding binding;
    private List<DailyRecordsCard> monthlyRecords;
    private RecordsHeader header;
    private RecordsHeaderAdapter headerAdapter;
    private MonthlyRecordsAdapter recordsAdapter;
    private SharedPreferences shared;
    private int year, month; // year, month for loaded records;

    public RecordsFragment() {
    }

    /* load latest monthly records from DB every time on resume, notify adapter */
    @Override
    public void onResume() {
        super.onResume();
        loadMonthlyRecords(year, month);
        headerAdapter.notifyDataSetChanged();
        recordsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getActivity().getSharedPreferences("budget", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecordsBinding.inflate(inflater, container, false);

        initTvMonth();
        initRvMonthlyRecords();
        initNewRecordBtn();

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
                // onMonthSet: update year, month; load new records, notify change
                this.year = year;
                this.month = month;
                binding.tvMonth.setText(DateUtils.getYearMonthStr(year, month));
                loadMonthlyRecords(year, month);
                headerAdapter.notifyDataSetChanged();
                recordsAdapter.notifyDataSetChanged();
            });
            dialog.show();
            dialog.setDialogPosition();
        });
    }

    /* set layout, header adapter and record list adapter for monthly records list */
    private void initRvMonthlyRecords() {
        // set linear layout manager
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.rvMonthlyRecords.setLayoutManager(manager);
        // init view models for providing data to headers and record list;
        monthlyRecords = new ArrayList<>();
        double budget = Double.parseDouble(shared.getString("budget", "0"));
        header = new RecordsHeader(budget, monthlyRecords);
        // set header and records adapter
        headerAdapter = new RecordsHeaderAdapter(getContext(), header);
        headerAdapter.setOnBudgetClickListener(v -> showBudgetSettingDialog());
        recordsAdapter = new MonthlyRecordsAdapter(getContext(), monthlyRecords);
        binding.rvMonthlyRecords.setAdapter(new ConcatAdapter(headerAdapter, recordsAdapter));
    }

    /* show budget setting dialog upon clicking budget surplus */
    private void showBudgetSettingDialog() {
        BudgetSettingDialog dialog = new BudgetSettingDialog(getContext());
        dialog.setOnBudgetConfirmListener(budget -> {
            // on budget set, update stored value, notify header to update budget surplus
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("budget", String.valueOf(budget));
            editor.apply();
            header.setBudget(budget);
            headerAdapter.notifyDataSetChanged();
        });
        dialog.show();
        dialog.setDialogPosition();
    }

    private void initNewRecordBtn() {
        /* register onClick listener for ibAddRecord: jump to NewRecordActivity */
        binding.ibAddRecord.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewRecordActivity.class);
            startActivity(intent);
        });
    }

    /* load records for given month; populate data for adapter; month: 0-11 */
    private void loadMonthlyRecords(int year, int month) {
        monthlyRecords.clear();
        // load monthly records
        RecordDao recordDao = MainApplication.getInstance().getMainDatabase().recordDao();
        List<Record> recordList = recordDao.getRecordsByMonth(year, month);
        // group monthly records by day, starting from latest day
        Map<Integer, List<Record>> map = new TreeMap<>((o1, o2) -> Integer.compare(o2, o1));
        for (Record record : recordList) {
            int dayOfMonth = record.getDayOfMonth();
            if (!map.containsKey(dayOfMonth))
                map.put(dayOfMonth, new ArrayList<>());
            map.get(dayOfMonth).add(record);
        }
        for (Map.Entry<Integer, List<Record>> entry : map.entrySet()) {
            String date = DateUtils.getDateStrYYYYMMDD(year, month, entry.getKey());
            monthlyRecords.add(new DailyRecordsCard(date, entry.getValue()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}