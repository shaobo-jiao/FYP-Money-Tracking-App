package com.example.finalyearproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.finalyearproject.adapters.NewRecordPagerAdapter;
import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.dao.RecordDao;
import com.example.finalyearproject.databinding.ActivityNewRecordBinding;
import com.google.android.material.tabs.TabLayoutMediator;
/* Activity for adding new record or updating an existing record
 *
 * */
public class NewRecordActivity extends AppCompatActivity {
    private com.example.finalyearproject.databinding.ActivityNewRecordBinding binding;
    private Record record = null; // null if adding new record, not null if updating existing one

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initRecord();
        initViewPager();
        initBackIcon();
    }

    /* if is to update an existing record from RecordDetailsPage, retrieve the id and query */
    private void initRecord() {
        int recordId = getIntent().getIntExtra("recordId", -1);
        if (recordId != -1) {
            RecordDao recordDao = MainApplication.getInstance().getMainDatabase().recordDao();
            record = recordDao.getRecordById(recordId); // query by id
        }
    }

    /* set adapter for the viewpager and link tabLayout with viewpager */
    private void initViewPager() {
        NewRecordPagerAdapter adapter = new NewRecordPagerAdapter(this, record);
        binding.vpNewRecord.setAdapter(adapter);
        new TabLayoutMediator(binding.tabNewRecord, binding.vpNewRecord,
                (tab, position) -> tab.setText(adapter.getTitle(position))).attach();
        // if is editing existing record, display NewExpense or NewIncome fragment depending on recordType;
        if (record != null) {
            binding.vpNewRecord.setCurrentItem(record.getRecordType() == Record.RECORD_EXPENSE ? 0 : 1);
        }
    }

    /* register onClick listener for ivBack: finish current activity */
    private void initBackIcon() {
        binding.ivBack.setOnClickListener(v -> finish());
    }
}