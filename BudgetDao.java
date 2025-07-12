package com.wyt.simpleaccounts.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.wyt.simpleaccounts.data.entity.Budget;

@Dao
public interface BudgetDao {
    @Insert
    void insert(Budget budget);

    @Update
    void update(Budget budget);

    @Query("SELECT * FROM budgets WHERE year = :year AND month = :month LIMIT 1")
    LiveData<Budget> getBudgetForMonth(int year, int month);

    @Query("SELECT * FROM budgets WHERE year = :year AND month = :month LIMIT 1")
    Budget getBudgetForMonthSync(int year, int month);
} 