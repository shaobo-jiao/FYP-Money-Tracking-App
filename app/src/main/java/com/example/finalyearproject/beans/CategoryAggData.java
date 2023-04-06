package com.example.finalyearproject.beans;

/* entity class representing each category's aggregation data required in ChartFragment */
public class CategoryAggData {
    private String name;  // name of category
    private int imageId; // image id of category
    private double total; // total amount of this category
    private double percentage; // percentage of this category's amount in total amount, (0, 100]

    public CategoryAggData(String name, int imageId, double total, double percentage) {
        this.name = name;
        this.imageId = imageId;
        this.total = total;
        this.percentage = percentage;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }

    public double getTotal() {
        return total;
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    public String toString() {
        return "CategoryAggData{" +
                "name='" + name + '\'' +
                ", imageId=" + imageId +
                ", total=" + total +
                ", percentage=" + percentage +
                '}';
    }
}
