package com.example.finalyearproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.dao.RecordDao;
import com.example.finalyearproject.databinding.ActivityRecordDetailsBinding;
import com.example.finalyearproject.utils.DateUtils;

import java.util.Locale;

public class RecordDetailsActivity extends AppCompatActivity {
    private static final String TAG = "RecordDetailsActivity";
    private com.example.finalyearproject.databinding.ActivityRecordDetailsBinding binding;
    private Record record;
    private int recordId;
    private RecordDao recordDao;


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        // load and display latest record info everytime coming back here;
        record = recordDao.getRecordById(recordId);
        setDisplayedRecordInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // retrieve recordId and create recordDao;
        recordId = getIntent().getIntExtra("recordId", -1);
        recordDao = MainApplication.getInstance().getMainDatabase().recordDao();

        initBtns();
    }

    /* display record info */
    private void setDisplayedRecordInfo() {
        binding.ivCategoryIcon.setImageResource(record.getCategoryImageId());
        binding.ivCategoryIcon.setImageTintList(getColorStateList(R.color.white));
        if (record.getRecordType() == Record.RECORD_EXPENSE)
            binding.ivCategoryIcon.setBackgroundTintList(getColorStateList(R.color.red));
        else
            binding.ivCategoryIcon.setBackgroundTintList(getColorStateList(R.color.green));
        binding.tvCategory.setText(record.getCategoryName());
        binding.tvType.setText(record.getRecordType() == Record.RECORD_EXPENSE ? "Expense" : "Income");
        binding.tvAmount.setText(String.format(Locale.US, "%.2f", record.getAmount()));
        binding.tvDate.setText(DateUtils.getDateStrYYYYMMDD(record.getYear(), record.getMonth(), record.getDayOfMonth()));
        binding.tvMemo.setText(record.getDesc());
    }

    /* register onClickListener for ivBack, ivDelete, btnEdit */
    private void initBtns() {
        binding.ivBack.setOnClickListener(v -> finish());
        binding.ivDelete.setOnClickListener(v -> {
            // show alert dialog to confirm deletion
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmation")
                    .setMessage("Are you sure to delete this record?")
                    .setNegativeButton("CANCEL", null)
                    .setPositiveButton("CONFIRM", (dialog, which) -> {
                        recordDao.deleteRecord(record);
                        finish();
                    });
            builder.create().show();
        });
        binding.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewRecordActivity.class);
            intent.putExtra("recordId", recordId);
            startActivity(intent);
        });
    }

}