package com.example.finalyearproject.beans;

import java.util.List;

/* View Model for providing data for DailyRecordsCard */
public class DailyRecordsCard {
    private String date; // date of records
    private List<Record> recordList; // all records within given day;

    public DailyRecordsCard(String date, List<Record> recordList) {
        this.date = date;
        this.recordList = recordList;
    }

    public String getDate() {
        return date;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    /* calculate and return total expense for this day */
    public double getTotalExpense() {
        double total = 0;
        for (Record record: recordList) {
            if (record.getRecordType() == Record.RECORD_EXPENSE) {
                total += record.getAmount();
            }
        }
        return total;
    }

    /* calculate and return total income for this day */
    public double getTotalIncome() {
        double total = 0;
        for (Record record: recordList) {
            if (record.getRecordType() == Record.RECORD_INCOME) {
                total += record.getAmount();
            }
        }
        return total;
    }


}
