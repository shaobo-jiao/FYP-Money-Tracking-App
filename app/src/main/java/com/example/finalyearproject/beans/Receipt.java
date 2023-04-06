package com.example.finalyearproject.beans;

import android.content.Context;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.File;
import java.nio.file.Path;

/* Entity class representing an receipt */
@Entity
public class Receipt {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String desc;
    private double amount;
    private int year;
    private int month; // 0-11
    private int dayOfMonth;
    private String imagePath; // absolute path to corresponding receipt image stored in app's private external storage;
    private String thumbnailPath;

    public Receipt() {
    }

    public Receipt(String name, String desc, double amount, int year, int month, int dayOfMonth, String imagePath, String thumbnailPath) {
        this.name = name;
        this.desc = desc;
        this.amount = amount;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.imagePath = imagePath;
        this.thumbnailPath = thumbnailPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    /* return path to the folder containing all receipt images stored */
    public static String getReceiptImgFolder(Context context) {
        return context.getExternalFilesDir("Receipts").toString();
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", amount=" + amount +
                ", year=" + year +
                ", month=" + month +
                ", dayOfMonth=" + dayOfMonth +
                ", imagePath='" + imagePath + '\'' +
                ", thumbnailPath='" + thumbnailPath + '\'' +
                '}';
    }
}
