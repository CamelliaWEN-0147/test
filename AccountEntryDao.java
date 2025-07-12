package com.wyt.simpleaccounts.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.wyt.simpleaccounts.data.entity.AccountEntry;

import java.util.List;

@Dao
public interface AccountEntryDao {
    @Insert
    void insert(AccountEntry entry);

    @Update
    void update(AccountEntry entry);

    @Delete
    void delete(AccountEntry entry);

    @Query("SELECT * FROM account_entries ORDER BY date DESC")
    LiveData<List<AccountEntry>> getAllEntries();

    @Query("SELECT * FROM account_entries WHERE type = :type ORDER BY date DESC")
    LiveData<List<AccountEntry>> getEntriesByType(String type);

    @Query("SELECT SUM(amount) FROM account_entries WHERE type = :type")
    LiveData<Double> getTotalByType(String type);
} 