package com.example.finalyearproject.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.example.finalyearproject.databinding.DatePickerDialogBinding;
import java.util.Calendar;

/* Custom Date Picker Dialog using CalenderView, same as official DatePickerDialog but removed header */
public class MyDatePickerDialog extends Dialog {

    private com.example.finalyearproject.databinding.DatePickerDialogBinding binding;
    private OnDateSetListener onDateSetListener;
    private Calendar initCalender = Calendar.getInstance(); // used to set initial date of cv;

    public MyDatePickerDialog(@NonNull Context context) {
        super(context);
    }

    public MyDatePickerDialog(@NonNull Context context, int year, int month, int dayOfMonth) {
        super(context);
        initCalender.set(Calendar.YEAR, year);
        initCalender.set(Calendar.MONTH, month);
        initCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DatePickerDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initCalenderView();
        initBtn();
    }

    /* init cv: register onDateChangeListener to change cv's internal date upon selected date change */
    private void initCalenderView() {
        // use passed year, month, dayOfMonth to set initial date of cv;
        binding.cvDate.setDate(initCalender.getTimeInMillis());

        // change cv's internal date when new date is selected
        binding.cvDate.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            view.setDate(calendar.getTimeInMillis());
        });
    }

    /* register onClick listener for the two buttons */
    private void initBtn() {
        binding.btnCancel.setOnClickListener(v -> cancel());
        binding.btnConfirm.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(binding.cvDate.getDate());
            onDateSetListener.onDateSet(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            cancel();
        });
    }


    public void setOnDateSetListener(OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    public interface OnDateSetListener {
        void onDateSet(int year, int month, int dayOfMonth);
    }


}
