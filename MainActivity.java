package com.wyt.simpleaccounts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wyt.simpleaccounts.adapter.AccountEntryAdapter;
import com.wyt.simpleaccounts.data.entity.AccountEntry;
import com.wyt.simpleaccounts.data.entity.Budget;
import com.wyt.simpleaccounts.util.CategoryIcons;
import com.wyt.simpleaccounts.viewmodel.AccountViewModel;
import com.wyt.simpleaccounts.viewmodel.BudgetViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private AccountViewModel accountViewModel;
    private BudgetViewModel budgetViewModel;
    private AccountEntryAdapter adapter;
    private TextView balanceText;
    private TextView incomeText;
    private TextView expenseText;
    private TextView budgetAmountText;
    private TextView remainingBudgetText;
    private TextView availableBudgetText;
    private TextView budgetWarningText;
    private TabLayout tabLayout;
    private View emptyView;
    private RecyclerView recyclerView;
    private double currentMonthExpense = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("简易记账");
        }

        // 初始化视图
        balanceText = findViewById(R.id.balanceText);
        incomeText = findViewById(R.id.incomeText);
        expenseText = findViewById(R.id.expenseText);
        budgetAmountText = findViewById(R.id.budgetAmountText);
        remainingBudgetText = findViewById(R.id.remainingBudgetText);
        availableBudgetText = findViewById(R.id.availableBudgetText);
        budgetWarningText = findViewById(R.id.budgetWarningText);
        recyclerView = findViewById(R.id.recyclerView);
        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        tabLayout = findViewById(R.id.tabLayout);
        emptyView = findViewById(R.id.emptyView);

        // 设置预算编辑按钮
        ImageButton editBudgetButton = findViewById(R.id.editBudgetButton);
        editBudgetButton.setOnClickListener(v -> showEditBudgetDialog());

        // 设置RecyclerView
        adapter = new AccountEntryAdapter();
        adapter.setOnItemClickListener(this::showEditEntryDialog);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化ViewModel
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        // 观察数据变化
        accountViewModel.getAllEntries().observe(this, this::updateEntries);
        accountViewModel.getTotalByType("收入").observe(this, total -> {
            double incomeTotal = total != null ? total : 0.0;
            incomeText.setText(String.format(Locale.getDefault(), "¥%.2f", incomeTotal));
            updateBalance();
        });
        accountViewModel.getTotalByType("支出").observe(this, total -> {
            double expenseTotal = total != null ? total : 0.0;
            currentMonthExpense = expenseTotal;
            expenseText.setText(String.format(Locale.getDefault(), "¥%.2f", expenseTotal));
            updateBalance();
            updateBudgetStatus();
        });

        // 观察预算变化
        budgetViewModel.getCurrentMonthBudget().observe(this, budget -> {
            if (budget != null) {
                budgetAmountText.setText(String.format(Locale.getDefault(), "预算: ¥%.2f", budget.getAmount()));
                updateBudgetStatus();
            } else {
                budgetAmountText.setText("未设置预算");
                remainingBudgetText.setText("");
                availableBudgetText.setVisibility(View.GONE);
                budgetWarningText.setVisibility(View.GONE);
                // 如果预算为空，尝试刷新预算数据
                budgetViewModel.refreshBudget();
            }
        });

        // 设置标签页切换监听
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // 全部
                        accountViewModel.getAllEntries().observe(MainActivity.this, 
                            MainActivity.this::updateEntries);
                        break;
                    case 1: // 收入
                        accountViewModel.getEntriesByType("收入").observe(MainActivity.this, 
                            MainActivity.this::updateEntries);
                        break;
                    case 2: // 支出
                        accountViewModel.getEntriesByType("支出").observe(MainActivity.this, 
                            MainActivity.this::updateEntries);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 设置添加按钮点击事件
        fab.setOnClickListener(v -> showAddEntryDialog());
    }

    private void updateEntries(List<AccountEntry> entries) {
        adapter.submitList(entries);
        emptyView.setVisibility(entries.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(entries.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void updateBalance() {
        try {
            String incomeStr = incomeText.getText().toString().replace("¥", "");
            String expenseStr = expenseText.getText().toString().replace("¥", "");
            double income = Double.parseDouble(incomeStr);
            double expense = Double.parseDouble(expenseStr);
            double balance = income - expense;
            balanceText.setText(String.format(Locale.getDefault(), "¥%.2f", balance));
        } catch (NumberFormatException e) {
            balanceText.setText("¥0.00");
        }
    }

    private void showAddEntryDialog() {
        showEntryDialog(null);
    }

    private void updateBudgetStatus() {
        Budget budget = budgetViewModel.getCurrentMonthBudget().getValue();
        if (budget != null) {
            double remaining = budget.getAmount() - currentMonthExpense;
            budgetAmountText.setText(String.format(Locale.getDefault(), "预算: ¥%.2f", budget.getAmount()));
            remainingBudgetText.setText(String.format(Locale.getDefault(), "剩余: ¥%.2f", remaining));
            
            // 计算本月剩余天数
            Calendar now = Calendar.getInstance();
            int currentDay = now.get(Calendar.DAY_OF_MONTH);
            int totalDays = now.getActualMaximum(Calendar.DAY_OF_MONTH);
            int remainingDays = totalDays - currentDay + 1; // 包含今天

            // 计算每天可用预算
            double dailyBudget = remaining / remainingDays;
            availableBudgetText.setText(String.format(Locale.getDefault(), "可用: ¥%.2f/天", dailyBudget));
            availableBudgetText.setVisibility(View.VISIBLE);
            
            if (remaining < 0) {
                budgetWarningText.setText("本月花销已超出预算");
                budgetWarningText.setVisibility(View.VISIBLE);
                availableBudgetText.setTextColor(ContextCompat.getColor(this, R.color.expense_red));
                remainingBudgetText.setTextColor(ContextCompat.getColor(this, R.color.expense_red));
            } else {
                budgetWarningText.setVisibility(View.GONE);
                availableBudgetText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                remainingBudgetText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            }
        } else {
            budgetAmountText.setText("未设置预算");
            remainingBudgetText.setText("");
            availableBudgetText.setVisibility(View.GONE);
            budgetWarningText.setVisibility(View.GONE);
        }
    }

    private void showEditBudgetDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_budget, null);
        TextInputEditText budgetInput = dialogView.findViewById(R.id.amountEditText);
        TextInputLayout budgetInputLayout = dialogView.findViewById(R.id.amountInputLayout);
        
        // 填充当前预算金额（如果有）
        Budget currentBudget = budgetViewModel.getCurrentMonthBudget().getValue();
        if (currentBudget != null) {
            budgetInput.setText(String.format(Locale.getDefault(), "%.2f", currentBudget.getAmount()));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("设置预算")
                .setView(dialogView)
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String budgetStr = budgetInput.getText() != null ? budgetInput.getText().toString().trim() : "";
                if (budgetStr.isEmpty()) {
                    budgetInputLayout.setError("请输入预算金额");
                    return;
                }

                try {
                    double budgetAmount = Double.parseDouble(budgetStr);
                    if (budgetAmount <= 0) {
                        budgetInputLayout.setError("预算金额必须大于0");
                        return;
                    }
                    budgetViewModel.setBudget(budgetAmount);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    budgetInputLayout.setError("请输入有效的金额");
                }
            });
        });

        dialog.show();
    }

    private void showEditEntryDialog(AccountEntry entry) {
        showEntryDialog(entry);
    }

    private void showEntryDialog(AccountEntry entry) {
        boolean isEdit = entry != null;
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_entry, null);
        TextInputEditText amountInput = dialogView.findViewById(R.id.amountInput);
        RadioGroup typeRadioGroup = dialogView.findViewById(R.id.typeRadioGroup);
        RadioButton incomeRadio = dialogView.findViewById(R.id.incomeRadio);
        RadioButton expenseRadio = dialogView.findViewById(R.id.expenseRadio);
        AutoCompleteTextView categoryInput = dialogView.findViewById(R.id.categoryInput);
        TextInputEditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);

        // 如果是编辑模式，填充现有数据
        if (isEdit) {
            amountInput.setText(String.format(Locale.getDefault(), "%.2f", entry.getAmount()));
            if (entry.getType().equals("收入")) {
                incomeRadio.setChecked(true);
            } else {
                expenseRadio.setChecked(true);
            }
            categoryInput.setText(entry.getCategory());
            descriptionInput.setText(entry.getDescription());
        }

        // 设置类别下拉列表
        String[] expenseCategories = CategoryIcons.getExpenseCategories();
        String[] incomeCategories = CategoryIcons.getIncomeCategories();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, 
                isEdit && entry.getType().equals("收入") ? incomeCategories : expenseCategories);
        categoryInput.setAdapter(categoryAdapter);

        // 根据收支类型切换类别列表
        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String[] categories = checkedId == R.id.incomeRadio ? incomeCategories : expenseCategories;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, categories);
            categoryInput.setAdapter(adapter);
            categoryInput.setText("", false);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(isEdit ? "修改记账" : "记一笔")
                .setView(dialogView)
                .setPositiveButton(isEdit ? "保存" : "确定", (dialog, which) -> {
                    String amountStr = amountInput.getText() != null ? amountInput.getText().toString() : "";
                    String category = categoryInput.getText() != null ? categoryInput.getText().toString() : "";
                    String description = descriptionInput.getText() != null ? descriptionInput.getText().toString() : "";

                    if (!amountStr.isEmpty() && !category.isEmpty()) {
                        AccountEntry newEntry = isEdit ? entry : new AccountEntry();
                        newEntry.setAmount(Double.parseDouble(amountStr));
                        newEntry.setType(typeRadioGroup.getCheckedRadioButtonId() == R.id.incomeRadio ? "收入" : "支出");
                        newEntry.setCategory(category);
                        newEntry.setDescription(description);
                        if (!isEdit) {
                            newEntry.setDate(new Date());
                        }
                        
                        if (isEdit) {
                            accountViewModel.update(newEntry);
                        } else {
                            accountViewModel.insert(newEntry);
                        }
                    }
                });

        // 如果是编辑模式，添加删除按钮
        if (isEdit) {
            builder.setNeutralButton("删除", (dialog, which) -> {
                new AlertDialog.Builder(this)
                    .setTitle("确认删除")
                    .setMessage("确定要删除这条记录吗？")
                    .setPositiveButton("确定", (dialogInterface, i) -> accountViewModel.delete(entry))
                    .setNegativeButton("取消", null)
                    .show();
            });
        }

        builder.setNegativeButton("取消", null)
               .show();
    }
}