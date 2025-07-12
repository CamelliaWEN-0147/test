package com.wyt.simpleaccounts.util;

import com.wyt.simpleaccounts.R;
import java.util.HashMap;
import java.util.Map;

public class CategoryIcons {
    private static final Map<String, Integer> categoryIcons = new HashMap<>();
    private static final Map<String, Integer> categoryColors = new HashMap<>();
    
    static {
        // 支出类别图标
        categoryIcons.put("餐饮", R.drawable.ic_food);
        categoryIcons.put("交通", R.drawable.ic_transport);
        categoryIcons.put("购物", R.drawable.ic_shopping);
        categoryIcons.put("娱乐", R.drawable.ic_entertainment);
        categoryIcons.put("学习", R.drawable.ic_education);
        categoryIcons.put("医疗", R.drawable.ic_medical);
        categoryIcons.put("住房", R.drawable.ic_house);
        categoryIcons.put("其他支出", R.drawable.ic_other_expense);
        
        // 收入类别图标
        categoryIcons.put("工资", R.drawable.ic_salary);
        categoryIcons.put("奖金", R.drawable.ic_bonus);
        categoryIcons.put("理财", R.drawable.ic_investment);
        categoryIcons.put("其他收入", R.drawable.ic_other_income);
        
        // 类别颜色
        categoryColors.put("餐饮", R.color.category_food);
        categoryColors.put("交通", R.color.category_transport);
        categoryColors.put("购物", R.color.category_shopping);
        categoryColors.put("娱乐", R.color.category_entertainment);
        categoryColors.put("学习", R.color.category_education);
        categoryColors.put("医疗", R.color.category_medical);
        categoryColors.put("住房", R.color.category_house);
        categoryColors.put("其他支出", R.color.category_other_expense);
        
        categoryColors.put("工资", R.color.category_salary);
        categoryColors.put("奖金", R.color.category_bonus);
        categoryColors.put("理财", R.color.category_investment);
        categoryColors.put("其他收入", R.color.category_other_income);
    }
    
    public static int getIconResourceForCategory(String category) {
        return categoryIcons.getOrDefault(category, R.drawable.ic_other_expense);
    }
    
    public static int getColorResourceForCategory(String category) {
        return categoryColors.getOrDefault(category, R.color.category_other_expense);
    }
    
    public static String[] getExpenseCategories() {
        return new String[]{"餐饮", "交通", "购物", "娱乐", "学习", "医疗", "住房", "其他支出"};
    }
    
    public static String[] getIncomeCategories() {
        return new String[]{"工资", "奖金", "理财", "其他收入"};
    }
} 