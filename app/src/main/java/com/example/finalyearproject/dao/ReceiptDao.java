package com.example.finalyearproject.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.finalyearproject.beans.Receipt;

import java.util.List;

/* Dao for managing receipt info with Receipt table in DB */
@Dao
public interface ReceiptDao {
    @Insert
    long insertReceipt(Receipt receipt);

    @Query("SELECT * FROM Receipt WHERE id = :id")
    Receipt getReceiptById(int id);

    @Query("SELECT * FROM Receipt WHERE year = :year AND month = :month ORDER BY id DESC")
    List<Receipt> getReceiptsByMonth(int year, int month);

    @Delete
    void deleteReceipt(Receipt receipt);

    @Query("DELETE FROM Receipt")
    void deleteAllReceipt();

    @Update
    void updateReceipt(Receipt receipt);
}
