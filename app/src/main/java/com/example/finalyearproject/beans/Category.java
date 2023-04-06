package com.example.finalyearproject.beans;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.finalyearproject.MainApplication;
import com.example.finalyearproject.R;
import com.example.finalyearproject.dao.CategoryDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Entity class representing a category */
@Entity
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id; // category id
    private String name; // name of category
    private int imageId; // category icon id
    private int recordType; // record type of this category, either EXPENSE(0) or INCOME(1)

    public Category(String name, int imageId, int recordType) {
        this.name = name;
        this.imageId = imageId;
        this.recordType = recordType;
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

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    /* get pre-defined categories (name+icon), to be inserted into DB */
    public static List<Category> getDefaultCategoryList() {
        List<Category> categoryList = new ArrayList<>();

        // add all categories for expense
        categoryList.add(new Category("Food", R.drawable.ic_food, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Shopping", R.drawable.ic_shopping, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Bills", R.drawable.ic_bills, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Transportation", R.drawable.ic_transportation, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Necessities", R.drawable.ic_necessities, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Supermarket", R.drawable.ic_supermarket, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Snacks", R.drawable.ic_snacks, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Sport", R.drawable.ic_sport, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Entertainment", R.drawable.ic_entertainment, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Stationery", R.drawable.ic_stationery, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Education", R.drawable.ic_education, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Books", R.drawable.ic_books, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Social", R.drawable.ic_social, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Housing", R.drawable.ic_housing, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Work", R.drawable.ic_work, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Clothes", R.drawable.ic_clothes, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Beauty", R.drawable.ic_beauty, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Health", R.drawable.ic_health, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Kids", R.drawable.ic_kids, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Wine", R.drawable.ic_wine, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Travel", R.drawable.ic_travel, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Car", R.drawable.ic_car, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Telephone", R.drawable.ic_telephone, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Gift", R.drawable.ic_gift, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Pet", R.drawable.ic_pet, Record.RECORD_EXPENSE));
        categoryList.add(new Category("Others", R.drawable.ic_others, Record.RECORD_EXPENSE));

        // add all categories for income
        categoryList.add(new Category("Salary", R.drawable.ic_salary, Record.RECORD_INCOME));
        categoryList.add(new Category("Part-Time", R.drawable.ic_part_time, Record.RECORD_INCOME));
        categoryList.add(new Category("Refunds", R.drawable.ic_refunds, Record.RECORD_INCOME));
        categoryList.add(new Category("Coupon", R.drawable.ic_coupon, Record.RECORD_INCOME));
        categoryList.add(new Category("Awards", R.drawable.ic_awards, Record.RECORD_INCOME));
        categoryList.add(new Category("Sale", R.drawable.ic_sale, Record.RECORD_INCOME));
        categoryList.add(new Category("Investment", R.drawable.ic_investment, Record.RECORD_INCOME));
        categoryList.add(new Category("Lottery", R.drawable.ic_lottery, Record.RECORD_INCOME));
        categoryList.add(new Category("Others", R.drawable.ic_others, Record.RECORD_INCOME));


        return categoryList;
    }

    /* return map of category name to category image id */
    public static Map<String, Integer> getCategoryNameImgMap() {
        CategoryDao categoryDao = MainApplication.getInstance().getMainDatabase().categoryDao();
        List<Category> allCategories = categoryDao.getAllCategories();
        Map<String, Integer> nameToImgMap = new HashMap<>();
        for (Category category: allCategories) {
            nameToImgMap.put(category.getName(), category.getImageId());
        }
        return nameToImgMap;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageId=" + imageId +
                ", recordType=" + recordType +
                '}';
    }
}
