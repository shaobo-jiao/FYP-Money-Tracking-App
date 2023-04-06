package com.example.finalyearproject;

import android.app.Application;
import android.content.ContentValues;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.finalyearproject.beans.Category;
import com.example.finalyearproject.database.MainDatabase;

import java.util.List;

/*
* MainApplication used for this app
* contains database reference
* */
public class MainApplication extends Application {
    private static MainApplication mApp;
    private MainDatabase mDatabase;

    /* return single instance of this activity */
    public static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;

        // insert all categories on database created;
        RoomDatabase.Callback rdc = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                // get default category list, manually insert all categories;
                List<Category> categoryList = Category.getDefaultCategoryList();
                for (Category category: categoryList) {
                    ContentValues cv = new ContentValues();
                    cv.put("name", category.getName());
                    cv.put("imageId", category.getImageId());
                    cv.put("recordType", category.getRecordType());
                    db.insert("Category", OnConflictStrategy.REPLACE, cv);
                }
            }
        };
        // built database
        mDatabase = Room.databaseBuilder(mApp, MainDatabase.class, "main")
                .addCallback(rdc)
                .addMigrations()
                .allowMainThreadQueries()
                .build();
    }


    public MainDatabase getMainDatabase() {
        return mDatabase;
    }

}
