package com.example.finalyearproject.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.finalyearproject.adapters.MonthsAdapter;
import com.example.finalyearproject.databinding.MonthPickerDialogBinding;


public class MonthPickerDialog extends Dialog {
    private com.example.finalyearproject.databinding.MonthPickerDialogBinding binding;
    private OnMonthSetListener onMonthSetListener;
    private final int initYear; // year when dialog is created
    private final int initMonth; // month when dialog is created, in range of 0-11
    private MonthsAdapter monthsAdapter;

    public MonthPickerDialog(@NonNull Context context, int initYear, int initMonth) {
        super(context);
        this.initYear = initYear;
        this.initMonth = initMonth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MonthPickerDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initTvYear();
        initRvMonths();
        initBtns();
    }

    /* init year displayed upon create */
    private void initTvYear() {
        binding.tvYear.setText(String.valueOf(initYear));
    }

    /* setup rvMonths and register onItemClickListener: execute onDateSet and close dialog */
    private void initRvMonths() {
        GridLayoutManager manager = new GridLayoutManager(getContext(), 4);
        binding.rvMonths.setLayoutManager(manager);
        monthsAdapter = new MonthsAdapter(getContext());
        monthsAdapter.setSelectedPos(initMonth);
        monthsAdapter.setOnItemClickListener((view, position) -> {
            int year = Integer.parseInt(binding.tvYear.getText().toString());
            onMonthSetListener.onMonthSet(year, position);
            cancel(); // close dialog
        });
        binding.rvMonths.setAdapter(monthsAdapter);
    }

    /* register onClickListener for ivPrev and ivNext: change year, change month bg */
    private void initBtns() {
        binding.ivPrev.setOnClickListener(v -> {
            int newYear = Integer.parseInt(binding.tvYear.getText().toString()) - 1;
            changeYear(newYear);
        });
        binding.ivNext.setOnClickListener(v -> {
            int newYear = Integer.parseInt(binding.tvYear.getText().toString()) + 1;
            changeYear(newYear);
        });
    }

    /* year++ or year-- and update month buttons color */
    private void changeYear(int newYear) {
        binding.tvYear.setText(String.valueOf(newYear));
        // if not initYear, change initMonth color to be grey; if yes, highlight month button
        if (newYear != initYear) {
            monthsAdapter.setSelectedPos(-1);
            monthsAdapter.notifyItemChanged(initMonth);
        }
        else {
            monthsAdapter.setSelectedPos(initMonth);
            monthsAdapter.notifyItemChanged(initMonth);
        }
    }

    public void setOnMonthSetListener(OnMonthSetListener onMonthSetListener) {
        this.onMonthSetListener = onMonthSetListener;
    }

    /* set dialog at top of screen, occupying full width */
    public void setDialogPosition() {
        Window window = getWindow();
        window.setGravity(Gravity.TOP);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // necessary for setting width
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public interface OnMonthSetListener {
        /* month: 0-11 to align with OnDateSetListener */
        void onMonthSet(int year, int month);
    }
}
