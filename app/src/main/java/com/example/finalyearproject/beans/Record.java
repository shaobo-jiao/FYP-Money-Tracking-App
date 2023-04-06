package com.example.finalyearproject.beans;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/* Entity class representing an expense or income record */
@Entity
public class Record {
    @Ignore
    public static final int RECORD_EXPENSE = 0;
    @Ignore
    public static final int RECORD_INCOME = 1;

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int recordType; // either Expense (0) or Income (1)
    private String categoryName;
    private int categoryImageId;
    private String desc; // memo or description of the record
    private double amount; // amount of money
    private int year;  // date of record: year
    private int month; // date of record: month (0-11)
    private int dayOfMonth;   // date of record: dayOfMonth

    public Record() {
    }
    public Record(int recordType, String categoryName, int categoryImageId, String desc, double amount, int year, int month, int dayOfMonth) {
        this.recordType = recordType;
        this.categoryName = categoryName;
        this.categoryImageId = categoryImageId;
        this.desc = desc;
        this.amount = amount;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryImageId() {
        return categoryImageId;
    }

    public void setCategoryImageId(int categoryImageId) {
        this.categoryImageId = categoryImageId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", recordType=" + recordType +
                ", categoryName='" + categoryName + '\'' +
                ", categoryImageId=" + categoryImageId +
                ", desc='" + desc + '\'' +
                ", amount=" + amount +
                ", year=" + year +
                ", month=" + month +
                ", dayOfMonth=" + dayOfMonth +
                '}';
    }
}
