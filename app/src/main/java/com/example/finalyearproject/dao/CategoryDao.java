package com.example.finalyearproject.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finalyearproject.beans.Category;

import java.util.List;

/* Dao for managing category date; */
@Dao
public interface CategoryDao {
    @Query("SELECT * FROM Category")
    List<Category> getAllCategories();

    @Query("SELECT * FROM Category WHERE recordType = 0")
    List<Category> getAllExpenseCategories();

    @Query("SELECT * FROM Category WHERE recordType = 1")
    List<Category> getAllIncomeCategories();

    @Insert
    void insertCategory(Category category);

    @Insert
    void insertCategoryList(List<Category> categoryList);
}
