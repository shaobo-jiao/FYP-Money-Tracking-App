package com.example.finalyearproject.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.finalyearproject.beans.CategoryAggData;
import com.example.finalyearproject.beans.Record;

import java.util.List;

/* Dao for managing expense and income records with Record table */
@Dao
public interface RecordDao {

    // insert one record
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRecord(Record record);

    // insert a list of records
    @Insert
    void insertRecordList(List<Record> recordList);

    @Query("SELECT * FROM Record where id = :id")
    Record getRecordById(int id);

    // query all records for given year-month ordered from latest
    @Query("SELECT * FROM Record WHERE year = :year AND month = :month ORDER BY year DESC, month DESC, dayOfMonth DESC")
    List<Record> getRecordsByMonth(int year, int month);

    @Query("SELECT * FROM Record " +
            "WHERE (year BETWEEN :startY AND :endY) " +
            "AND (month BETWEEN :startM AND :endM) " +
            "AND (dayOfMonth BETWEEN :startD AND :endD) " +
            "ORDER BY year DESC, month DESC, dayOfMonth DESC")
    List<Record> getRecordsByDateRange(int startY, int startM, int startD, int endY, int endM, int endD);

    // delete one record
    @Delete
    void deleteRecord(Record record);

    // delete all records
    @Query("DELETE FROM Record")
    void deleteAllRecords();

    // query all records for given date ordered from latest (new record has bigger id)
    @Query("SELECT * FROM Record WHERE year = :year AND month = :month AND dayOfMonth = :dayOfMonth ORDER BY id DESC")
    List<Record> getRecordsByDayOfMonth(int year, int month, int dayOfMonth);

    // query all records
    @Query("SELECT * FROM Record ORDER BY id DESC")
    List<Record> getAllRecords();

    // update one record
    @Update
    void updateRecord(Record record);

    // get category's total amount and percentage for given month, together with category name and icon;
    @Query("SELECT categoryName as name, imageId, total, " +
            "  total / (SELECT SUM(amount) FROM Record WHERE year = :year AND month = :month " +
            "  AND recordType = :recordType) * 100 AS percentage " +
            "FROM (SELECT categoryName, SUM(amount) as total FROM Record " +
            "  WHERE year = :year AND month = :month AND recordType = :recordType " +
            "  GROUP BY categoryName) AS CA " +
            "LEFT OUTER JOIN Category AS C ON CA.categoryName = C.name " +
            "WHERE C.recordType = :recordType " +
            "ORDER BY total DESC")
    List<CategoryAggData> getCategoryAggData(int year, int month, int recordType);

}
