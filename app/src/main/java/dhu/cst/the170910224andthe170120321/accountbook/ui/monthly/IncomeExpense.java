package dhu.cst.the170910224andthe170120321.accountbook.ui.monthly;

import androidx.annotation.NonNull;

import java.math.BigDecimal;

public class IncomeExpense {
    private BigDecimal income;
    private BigDecimal expense;

    IncomeExpense(BigDecimal income, BigDecimal expense) {
        this.income = income;
        this.expense = expense;
    }

    IncomeExpense(double income, double expense) {
        this.income = new BigDecimal(income);
        this.expense = new BigDecimal(expense);
    }

    IncomeExpense(float income, float expense) {
        this.income = new BigDecimal(income);
        this.expense = new BigDecimal(expense);
    }

    public BigDecimal getIncome() {
        return this.income;
    }

    public BigDecimal getExpense() {
        return this.expense;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public void setIncome(double income) {
        this.income = new BigDecimal(income);
    }

    public void setIncome(float income) {
        this.income = new BigDecimal(income);
    }

    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }

    public void setExpense(double expense) {
        this.expense = new BigDecimal(expense);
    }

    public void setExpense(float expense) {
        this.expense = new BigDecimal(expense);
    }

    public void addIncome(BigDecimal income) {
        this.income = this.income.add(income);
    }

    public void addExpense(BigDecimal expense) {
        this.expense = this.expense.add(expense);
    }

    @NonNull
    @Override
    public String toString() {
        return income.doubleValue() + " " + expense.doubleValue();
    }
}
