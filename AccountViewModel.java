package com.wyt.simpleaccounts.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.wyt.simpleaccounts.data.entity.AccountEntry;
import com.wyt.simpleaccounts.data.repository.AccountRepository;
import java.util.List;

public class AccountViewModel extends AndroidViewModel {
    private final AccountRepository repository;
    private final LiveData<List<AccountEntry>> allEntries;

    public AccountViewModel(Application application) {
        super(application);
        repository = new AccountRepository(application);
        allEntries = repository.getAllEntries();
    }

    public LiveData<List<AccountEntry>> getAllEntries() {
        return allEntries;
    }

    public LiveData<List<AccountEntry>> getEntriesByType(String type) {
        return repository.getEntriesByType(type);
    }

    public LiveData<Double> getTotalByType(String type) {
        return repository.getTotalByType(type);
    }

    public void insert(AccountEntry entry) {
        repository.insert(entry);
    }

    public void update(AccountEntry entry) {
        repository.update(entry);
    }

    public void delete(AccountEntry entry) {
        repository.delete(entry);
    }
} 