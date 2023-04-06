package com.example.finalyearproject.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.finalyearproject.MainApplication;
import com.example.finalyearproject.R;
import com.example.finalyearproject.ReceiptDetailsActivity;
import com.example.finalyearproject.RecordDetailsActivity;
import com.example.finalyearproject.ScanActivity;
import com.example.finalyearproject.adapters.MonthlyReceiptsAdapter;
import com.example.finalyearproject.beans.Receipt;
import com.example.finalyearproject.dao.ReceiptDao;
import com.example.finalyearproject.databinding.FragmentReceiptsBinding;
import com.example.finalyearproject.dialogs.MonthPickerDialog;
import com.example.finalyearproject.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ReceiptsFragment extends Fragment {
    private static final String TAG = "ReceiptFragment";
    private com.example.finalyearproject.databinding.FragmentReceiptsBinding binding;
//    private ActivityResultLauncher<Intent> scanLauncher;
    private int year, month; // selected year-month for displayed receipts
    private List<Receipt> monthlyReceipts;
    private MonthlyReceiptsAdapter receiptsAdapter;

    public ReceiptsFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        // load and display latest receipts when coming back here
        loadMonthlyReceipts(year, month);
        receiptsAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReceiptsBinding.inflate(inflater, container, false);

        initTvMonth();
        initRvMonthlyReceipts();
        initScanBtn();

        return binding.getRoot();
    }

    /* setup initial year, month, register onClickListener for tvMonth */
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
                // onMonthSet: update year, month; load new receipts, notify change
                this.year = year;
                this.month = month;
                binding.tvMonth.setText(DateUtils.getYearMonthStr(year, month));
                loadMonthlyReceipts(year, month);
                receiptsAdapter.notifyDataSetChanged();
            });
            dialog.show();
            dialog.setDialogPosition();
        });
    }

    /* set rcReceipts' layout, adapter */
    private void initRvMonthlyReceipts() {
        // set layout
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.rvMonthlyReceipts.setLayoutManager(manager);
        // init adapter with empty data; load actual data in onResume
        monthlyReceipts = new ArrayList<>();
        receiptsAdapter = new MonthlyReceiptsAdapter(getContext(), monthlyReceipts);
        binding.rvMonthlyReceipts.setAdapter(receiptsAdapter);
    }

    /* register onClickListener for ibScan: scan and import receipt */
    private void initScanBtn() {
        ActivityResultLauncher<Intent> scanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        // retrieve new record and receipt id from ScanActivity, launch both details activity;
                        Intent data = result.getData();
                        int newReceiptId = data.getIntExtra("newReceiptId", -1);
                        int newRecordId = data.getIntExtra("newRecordId", -1);
                        Intent recordIntent = new Intent(getContext(), RecordDetailsActivity.class);
                        recordIntent.putExtra("recordId", newRecordId);
                        Intent receiptIntent = new Intent(getContext(), ReceiptDetailsActivity.class);
                        receiptIntent.putExtra("receiptId", newReceiptId);
                        getContext().startActivities(new Intent[]{recordIntent, receiptIntent});
                    }
                });
        binding.ibScan.setOnClickListener(v -> {
            // start scan activity to import new receipts, alert dialog to select image source
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Image Source")
                    .setMessage("Open camera or pick from gallery?")
                    .setNegativeButton("Gallery", (dialog, which) -> {
                        Intent intent = new Intent(getContext(), ScanActivity.class);
                        intent.putExtra("fromGallery", true);
                        scanLauncher.launch(intent);
                    })
                    .setPositiveButton("Camera", (dialog, which) -> {
                        Intent intent = new Intent(getContext(), ScanActivity.class);
                        intent.putExtra("fromGallery", false);
                        scanLauncher.launch(intent);
                    });
            builder.create().show();
        });
    }

    /* load monthly receipts from DB for given year, month  */
    private void loadMonthlyReceipts(int year, int month) {
        monthlyReceipts.clear();
        ReceiptDao receiptDao = MainApplication.getInstance().getMainDatabase().receiptDao();
        monthlyReceipts.addAll(receiptDao.getReceiptsByMonth(year, month));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d(TAG, "Receipt Fragment destroyed");
    }
}