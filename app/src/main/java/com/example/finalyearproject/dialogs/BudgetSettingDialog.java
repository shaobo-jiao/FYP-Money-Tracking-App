package com.example.finalyearproject.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.finalyearproject.R;
import com.example.finalyearproject.databinding.BudgetSettingDialogBinding;

/* Dialog for setting budget */
public class BudgetSettingDialog extends Dialog {

    private com.example.finalyearproject.databinding.BudgetSettingDialogBinding binding;
    private OnBudgetConfirmListener onBudgetConfirmListener;

    public BudgetSettingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BudgetSettingDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initClickEvents();
    }

    private void initClickEvents() {
        binding.ivClose.setOnClickListener(v -> {
            cancel();
        });
        binding.btnConfirm.setOnClickListener(v -> {
            String budgetText = binding.etBudget.getText().toString();
            if (budgetText.equals("")) {
                Toast.makeText(getContext(), "Budget amount cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            double budget = Double.parseDouble(budgetText);
            if (budget < 0) {
                Toast.makeText(getContext(), "Budget amount must be positive", Toast.LENGTH_SHORT).show();
                return;
            }
            if (onBudgetConfirmListener != null)
                onBudgetConfirmListener.onBudgetConfirm(budget);
            cancel();
        });
    }

    /* set dialog at bottom, occupying full width, auto show soft keyboard*/
    public void setDialogPosition() {
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void setOnBudgetConfirmListener(OnBudgetConfirmListener onBudgetConfirmListener) {
        this.onBudgetConfirmListener = onBudgetConfirmListener;
    }

    public interface OnBudgetConfirmListener {
        void onBudgetConfirm(double budget);
    }

}
