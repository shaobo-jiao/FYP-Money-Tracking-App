package com.example.finalyearproject.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.finalyearproject.beans.Category;
import com.example.finalyearproject.beans.Receipt;
import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.dao.CategoryDao;
import com.example.finalyearproject.dao.ReceiptDao;
import com.example.finalyearproject.dao.RecordDao;

/* Database for all tables and getting DAOs used in this app: Category, Record, Receipt */
@Database(entities = {Category.class, Record.class, Receipt.class}, version = 1, exportSchema = false)
public abstract class MainDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();

    public abstract RecordDao recordDao();

    public abstract ReceiptDao receiptDao();
}
