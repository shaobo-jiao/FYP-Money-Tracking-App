package com.example.finalyearproject.fragments;

import com.example.finalyearproject.beans.Record;

public class NewExpenseFragment extends NewRecordFragment {

    public NewExpenseFragment(Record record) {
        super(record);
    }

    @Override
    protected int getRecordType() {
        return Record.RECORD_EXPENSE;
    }
}