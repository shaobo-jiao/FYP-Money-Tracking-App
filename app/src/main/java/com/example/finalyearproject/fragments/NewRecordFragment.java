package com.example.finalyearproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.finalyearproject.MainApplication;
import com.example.finalyearproject.adapters.CategoryAdapter;
import com.example.finalyearproject.beans.Category;
import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.components.CustomKeyboard;
import com.example.finalyearproject.dao.CategoryDao;
import com.example.finalyearproject.dao.RecordDao;
import com.example.finalyearproject.databinding.FragmentNewRecordBinding;

import java.util.List;

public abstract class NewRecordFragment extends Fragment {

    private com.example.finalyearproject.databinding.FragmentNewRecordBinding binding;
    private CustomKeyboard keyboard;
    private CategoryAdapter adapter;
    private final Record record; // record to be inserted or updated
    private final boolean toEdit; // true if is to edit an old receipt

    public NewRecordFragment(Record record) {
        if (record == null) {
            // to create new record
            this.record = new Record();
            this.toEdit = false;
        }
        else {
            // to edit an existing record
            this.record = record;
            toEdit = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        keyboard.hideKeyboard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewRecordBinding.inflate(inflater, container, false);
        keyboard = new CustomKeyboard(getContext(), binding.customKeyboard);

        initRvCategories();
        setKeyboardOnConfirmListener();
        initUsingOldRecord();

        return binding.getRoot();
    }

    /* Set keyboard's onConfirmListener: insert a new record and finish activity */
    private void setKeyboardOnConfirmListener() {
        keyboard.setOnConfirmListener(() -> {
            // extract necessary info for a new record, amount cannot be 0
            double amount = keyboard.getAmount();
            if (amount == 0) {
                Toast.makeText(getContext(), "Please enter the amount", Toast.LENGTH_SHORT).show();
                return;
            }
            String desc = keyboard.getDesc();
            Category category = adapter.getCategoryAt(adapter.getSelectedPosition());
            // extract date
            String date = keyboard.getDate(); // return date in dd/MM/yyyy
            int year, month, dayOfMonth;
            String[] parts = date.split("/");
            dayOfMonth = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]) - 1; // month displayed is [1-12]
            year = Integer.parseInt(parts[2]);
            // set record info and insert/update into DB
            record.setRecordType(getRecordType());
            record.setCategoryName(category.getName());
            record.setCategoryImageId(category.getImageId());
            record.setDesc(desc);
            record.setAmount(amount);
            record.setYear(year);
            record.setMonth(month);
            record.setDayOfMonth(dayOfMonth);
            RecordDao recordDao = MainApplication.getInstance().getMainDatabase().recordDao();
            // insert new or update old one
            recordDao.insertRecord(record);
            // finish current activity;
            getActivity().finish();
        });
    }

    /* set layout and adapter for rv_categories */
    private void initRvCategories() {
        // set layout for rvCategories:
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        binding.rvCategories.setLayoutManager(layoutManager);
        // load all categories for current record type
        List<Category> categoryList = loadAllCategories();
        // set adapter and item click listener: change item color, update keyboard's category icon
        adapter = new CategoryAdapter(getContext(), categoryList, getRecordType());
        adapter.setOnItemClickedListener((view, position) -> {
            int oldSelectedPos = adapter.getSelectedPosition();
            adapter.setSelectedPosition(position);
            adapter.notifyItemChanged(position);
            if (oldSelectedPos != -1)
                adapter.notifyItemChanged(oldSelectedPos);
            keyboard.setIvCategoryIconResource(categoryList.get(position).getImageId());
            keyboard.setIvCategoryIconBgColor(getRecordType());
            keyboard.showKeyboard();
        });
        binding.rvCategories.setAdapter(adapter);
    }

    /* if is to edit current receipt, initialize keyboard's contents with old receipt */
    private void initUsingOldRecord() {
        if (toEdit) {
            int categoryPos = adapter.getPositionOfCategory(record.getCategoryName());
            adapter.setSelectedPosition(categoryPos);
            adapter.notifyItemChanged(categoryPos);
            keyboard.setIvCategoryIconResource(record.getCategoryImageId());
            keyboard.setIvCategoryIconBgColor(record.getRecordType());
            keyboard.setDesc(record.getDesc());
            keyboard.setAmount(record.getAmount());
            keyboard.setDate(record.getYear(), record.getMonth(), record.getDayOfMonth());
            keyboard.showKeyboard();
        }
    }

    /* load all categories for either expense or income */
    private List<Category> loadAllCategories() {
        CategoryDao categoryDao = MainApplication.getInstance().getMainDatabase().categoryDao();
        if (getRecordType() == Record.RECORD_EXPENSE)
            return categoryDao.getAllExpenseCategories();
        else
            return categoryDao.getAllIncomeCategories();
    }

    /* get recordType, either 0 or 1 */
    protected abstract int getRecordType();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
