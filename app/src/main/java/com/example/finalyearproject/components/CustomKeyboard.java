package com.example.finalyearproject.components;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.finalyearproject.R;
import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.databinding.CustomKeyboardBinding;
import com.example.finalyearproject.dialogs.MyDatePickerDialog;
import com.example.finalyearproject.utils.DateUtils;

import java.util.Calendar;
import java.util.Locale;
/* A custom keyboard class used in "Adding new record" page; */
public class CustomKeyboard {
    private final Context context;
    private final CustomKeyboardBinding binding;
    private OnConfirmListener onConfirmListener;

    public CustomKeyboard(Context context, CustomKeyboardBinding binding) {
        this.context = context;
        this.binding = binding;
        initKeysOnClickListener();
        initEtDesc();
    }

    /* initialising on click listener for keys/buttons on the keyboard */
    private void initKeysOnClickListener() {
        setupBtnDigits();
        setupBtnDot();
        setupBtnDate();
        setupBtnClear();
        setupBtnBackspace();
        setupBtnConfirm();
    }
    private void setupBtnDigits() {
        View.OnClickListener digitOnClickListener = v -> {
            // 0-9 pressed. allow only 2 decimal places;
            String amountText = binding.tvAmount.getText().toString();
            String digit = ((Button) v).getText().toString();
            if (amountText.equals("0")) {
                setTvAmountText(digit);
            }
            else if (amountText.indexOf('.') == -1) {
                // no dot: append new digit if length < 8;
                if (amountText.length() < 8) {
                    amountText += digit;
                    setTvAmountText(amountText);
                }
            }
            else if (amountText.length() - 1 - amountText.indexOf('.') < 2) {
                // dot before: append new digit if less than 2 digits after dot
                amountText += digit;
                setTvAmountText(amountText);
            }
        };
        binding.btn0.setOnClickListener(digitOnClickListener);
        binding.btn1.setOnClickListener(digitOnClickListener);
        binding.btn2.setOnClickListener(digitOnClickListener);
        binding.btn3.setOnClickListener(digitOnClickListener);
        binding.btn4.setOnClickListener(digitOnClickListener);
        binding.btn5.setOnClickListener(digitOnClickListener);
        binding.btn6.setOnClickListener(digitOnClickListener);
        binding.btn7.setOnClickListener(digitOnClickListener);
        binding.btn8.setOnClickListener(digitOnClickListener);
        binding.btn9.setOnClickListener(digitOnClickListener);
    }
    private void setupBtnDot() {
        binding.btnDot.setOnClickListener(v -> {
            // dot pressed, only allow one dot: append dot if there's no dot before
            String amountText = binding.tvAmount.getText().toString();
            if (amountText.indexOf('.') == -1) {
                amountText += '.';
                setTvAmountText(amountText);
            }
        });
    }
    private void setupBtnDate() {
        binding.btnDate.setOnClickListener(v -> {
            // create a custom date picker dialog using pre-selected date, set date
            MyDatePickerDialog dialog;
            String date = binding.btnDate.getText().toString();
            if (date.equals("Today")) {
                dialog = new MyDatePickerDialog(context);
            }
            else {
                // use pre-selected date as initial date of the month picker dialog;
                String[] splits = date.split("/");
                dialog = new MyDatePickerDialog(context, Integer.parseInt(splits[2]), Integer.parseInt(splits[1]) - 1, Integer.parseInt(splits[0]));
            }
            // if date selected is today: set text to "Today", otherwise set to "dd/mm/yyyy"
            dialog.setOnDateSetListener(this::setDate);
            dialog.show();
        });
    }
    private void setupBtnClear() {
        binding.btnClear.setOnClickListener(v -> {
            setTvAmountText("");
        });
    }
    private void setupBtnBackspace() {
        binding.ibBackspace.setOnClickListener(v -> {
            String amountText = binding.tvAmount.getText().toString();
            // remove last char;
            if (amountText.length() > 0)
                amountText = amountText.substring(0, amountText.length() - 1);
            setTvAmountText(amountText);
        });
    }
    private void setupBtnConfirm() {
        binding.ibConfirm.setOnClickListener(v -> {
            if (onConfirmListener != null)
                onConfirmListener.onConfirm();
        });
    }

    /* set etDesc to clear focus (remove blinking cursor) and hide soft keyboard after pressed "Done" button */
    private void initEtDesc() {
        EditText etDesc = binding.etDesc;
        etDesc.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etDesc.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                etDesc.clearFocus();
                return true;
            }
            return false;
        });
    }

    public void showKeyboard() {
        binding.getRoot().setVisibility(View.VISIBLE);
    }
    public void hideKeyboard() {
        binding.getRoot().setVisibility(View.GONE);
    }

    public void setIvCategoryIconResource(int resId) {
        binding.ivCategoryIcon.setImageResource(resId);
    }
    public void setIvCategoryIconBgColor(int recordType) {
        if (recordType == Record.RECORD_EXPENSE) {
            binding.ivCategoryIcon.setBackgroundTintList(context.getColorStateList(R.color.red));
        }
        else {
            binding.ivCategoryIcon.setBackgroundTintList(context.getColorStateList(R.color.green));
        }
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    private void setTvAmountText(String amountText) {
        if (amountText.length() == 0)
            amountText = "0";
        binding.tvAmount.setText(amountText);
    }
    public void setAmount(double amount) {
        binding.tvAmount.setText(String.format(Locale.US, "%.2f", amount));
    }
    public double getAmount() {
        return Double.parseDouble(binding.tvAmount.getText().toString());
    }

    public void setDesc(String desc) {
        binding.etDesc.setText(desc);
    }
    public String getDesc() {
        return binding.etDesc.getText().toString();
    }

    /* set date shown on btnDate */
    public void setDate(int year, int month, int dayOfMonth) {
        // if today: set text as "Today", otherwise as dd/MM/yyyy
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == year
                && calendar.get(Calendar.MONTH) == month
                && calendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
            binding.btnDate.setText(R.string.keyboard_init_date);
        }
        else {
            binding.btnDate.setText(DateUtils.getDateStrDDMMYYYY(year, month, dayOfMonth));
        }
    }
    /* get date from btnDate's text, in form of dd/mm/yyyy */
    public String getDate() {
        String date = binding.btnDate.getText().toString();
        if (date.equals("Today")) {
            Calendar calendar = Calendar.getInstance();
            date = DateUtils.getDateStrDDMMYYYY(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        return date;
    }

    public interface OnConfirmListener {
        void onConfirm();
    }
}
