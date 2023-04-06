package com.example.finalyearproject.fragments;

import com.example.finalyearproject.beans.Record;

public class NewIncomeFragment extends NewRecordFragment {

    public NewIncomeFragment(Record record) {
        super(record);
    }

    @Override
    protected int getRecordType() {
        return Record.RECORD_INCOME;
    }
}