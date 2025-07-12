package com.wyt.simpleaccounts.data.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.wyt.simpleaccounts.data.AppDatabase;
import com.wyt.simpleaccounts.data.dao.BudgetDao;
import com.wyt.simpleaccounts.data.entity.Budget;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetRepository {
    private static final String TAG = "BudgetRepository";
    private final BudgetDao budgetDao;
    private final ExecutorService executorService;

    public BudgetRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        budgetDao = db.budgetDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void getBudgetForMonth(int year, int month, OnBudgetLoadedCallback callback) {
        executorService.execute(() -> {
            try {
                Budget budget = budgetDao.getBudgetForMonthSync(year, month);
                Log.d(TAG, String.format("加载预算数据 - 年: %d, 月: %d, 结果: %s", 
                    year, month, budget != null ? "成功" : "未找到"));
                callback.onBudgetLoaded(budget);
            } catch (Exception e) {
                Log.e(TAG, "加载预算数据失败", e);
                callback.onBudgetLoaded(null);
            }
        });
    }

    public void setBudget(Budget budget, Runnable onComplete) {
        executorService.execute(() -> {
            try {
                Budget existingBudget = budgetDao.getBudgetForMonthSync(budget.getYear(), budget.getMonth());
                if (existingBudget != null) {
                    existingBudget.setAmount(budget.getAmount());
                    budgetDao.update(existingBudget);
                    Log.d(TAG, String.format("更新预算 - 年: %d, 月: %d, 金额: %.2f", 
                        budget.getYear(), budget.getMonth(), budget.getAmount()));
                } else {
                    budgetDao.insert(budget);
                    Log.d(TAG, String.format("新增预算 - 年: %d, 月: %d, 金额: %.2f", 
                        budget.getYear(), budget.getMonth(), budget.getAmount()));
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            } catch (Exception e) {
                Log.e(TAG, "设置预算失败", e);
            }
        });
    }

    public interface OnBudgetLoadedCallback {
        void onBudgetLoaded(Budget budget);
    }
} 