package com.example.finalyearproject.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.fragments.NewExpenseFragment;
import com.example.finalyearproject.fragments.NewIncomeFragment;

public class NewRecordPagerAdapter extends FragmentStateAdapter {

    private final String[] titles = new String[]{"Expense", "Income"};
    private final Record record; // null if adding new record, not null if updating existing one

    public NewRecordPagerAdapter(@NonNull FragmentActivity fragmentActivity, Record record) {
        super(fragmentActivity);
        this.record = record;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new NewExpenseFragment(record);
        else if (position == 1)
            return new NewIncomeFragment(record);
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public String getTitle(int position) {
        /* get title for page at given position: either 0 or 1*/
        return titles[position];
    }
}
