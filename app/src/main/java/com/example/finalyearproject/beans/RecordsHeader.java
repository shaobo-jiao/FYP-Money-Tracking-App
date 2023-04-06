package com.example.finalyearproject.beans;

import com.example.finalyearproject.utils.DateUtils;

import java.util.List;
import java.util.Locale;

/* View Model for providing data for Records header
*  inputs: List<DailyRecordsCard> monthlyRecords;
*  outputs: budget surplus, total_expense, total_income
* */
public class RecordsHeader {
    private double budget;
    private List<DailyRecordsCard> monthlyRecords;

    public RecordsHeader(double budget, List<DailyRecordsCard> monthlyRecords) {
        this.budget = budget;
        this.monthlyRecords = monthlyRecords;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
    /* use monthlyRecords to get total expense in this month*/
    public double getTotalExpense() {
        if (monthlyRecords.isEmpty())
            return 0;
        double total = 0;
        for (DailyRecordsCard card: monthlyRecords) {
            total += card.getTotalExpense();
        }
        return total;
    }
    /* use monthlyRecords to get total income in this month*/
    public double getTotalIncome() {
        if (monthlyRecords.isEmpty())
            return 0;
        double total = 0;
        for (DailyRecordsCard card: monthlyRecords) {
            total += card.getTotalIncome();
        }
        return total;
    }
    /* compute and return budget surplus. return 0 if budget not set */
    public double getBudgetSurplus() {
        if (budget == 0)
            return 0;
        return budget - getTotalExpense();
    }
}
