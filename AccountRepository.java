package com.wyt.simpleaccounts.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.wyt.simpleaccounts.data.AppDatabase;
import com.wyt.simpleaccounts.data.dao.AccountEntryDao;
import com.wyt.simpleaccounts.data.entity.AccountEntry;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountRepository {
    private ExecutorService executorService;

    public AccountRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        accountEntryDao = db.accountEntryDao();
        allEntries = accountEntryDao.getAllEntries();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<AccountEntry>> getAllEntries() {
        return allEntries;
    }

    public LiveData<List<AccountEntry>> getEntriesByType(String type) {
        return accountEntryDao.getEntriesByType(type);
    }

    public LiveData<Double> getTotalByType(String type) {
        return accountEntryDao.getTotalByType(type);
    }

    public void insert(AccountEntry entry) {
        executorService.execute(() -> accountEntryDao.insert(entry));
    }

    public void update(AccountEntry entry) {
        executorService.execute(() -> accountEntryDao.update(entry));
    }

    public void delete(AccountEntry entry) {
        executorService.execute(() -> accountEntryDao.delete(entry));
    }
} 