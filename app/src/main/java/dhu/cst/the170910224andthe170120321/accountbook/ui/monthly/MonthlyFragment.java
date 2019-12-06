package dhu.cst.the170910224andthe170120321.accountbook.ui.monthly;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import dhu.cst.the170910224andthe170120321.accountbook.R;

public class MonthlyFragment extends Fragment {

    private MonthlyViewModel monthlyViewModel;
    private SQLiteDatabase sqLiteDatabaseRead;
    private TextView amountIncomeText;
    private TextView amountExpenseText;
    private EditText editText;
    private Button searchButton;
    private PieChart pieChartViewExpense;
    private PieChart pieChartViewIncome;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        monthlyViewModel =
                ViewModelProviders.of(this).get(MonthlyViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_monthly, container, false);

        amountIncomeText = root.findViewById(R.id.textIncomeAmount);
        amountExpenseText = root.findViewById(R.id.textExpenseAmount);
        editText = root.findViewById(R.id.searchYearly);
        searchButton = root.findViewById(R.id.searchYearlyButton);
        pieChartViewIncome = root.findViewById(R.id.pieChartIncome);
        pieChartViewExpense = root.findViewById(R.id.pieChartExpense);

        String dailyDatabasePath = getActivity().getFilesDir().getPath() + "/statement.db3";
        sqLiteDatabaseInitialization(dailyDatabasePath);

        List<IncomeExpense> incomeExpenseList = addIncomeExpenseToList();
        FromListAddingToPieChart(pieChartViewIncome, incomeExpenseList, "income");
        FromListAddingToPieChart(pieChartViewExpense, incomeExpenseList, "expense");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int year = Integer.parseInt(editText.getText().toString());
                    List<IncomeExpense> incomeExpenseList = addIncomeExpenseToList(year);
                    FromListAddingToPieChart(pieChartViewIncome, incomeExpenseList, "income");
                    FromListAddingToPieChart(pieChartViewExpense, incomeExpenseList, "expense");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        monthlyViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    private void sqLiteDatabaseInitialization(final String dailyDatabasePath) {
        sqLiteDatabaseRead = SQLiteDatabase.openOrCreateDatabase(dailyDatabasePath, null);
    }

    private List<IncomeExpense> addIncomeExpenseToList() {
        return addIncomeExpenseToList(Calendar.getInstance().get(Calendar.YEAR));
    }

    private List<IncomeExpense> addIncomeExpenseToList(final int year) {
        List<IncomeExpense> incomeExpenseList = new ArrayList<IncomeExpense>() {{
            // this year only
            for (int i = 0; i < 12 + 1; ++i) {
                add(new IncomeExpense(new BigDecimal(0), new BigDecimal(0)));
            }
        }};

        Cursor cursor = sqLiteDatabaseRead.rawQuery(
                "select time, type, money from statement where time like \'" + year + "%';", null);
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex("time"))
                    .replace(",", "");
            int month = Integer.parseInt(date.substring(5, 7)); // get month
            String type = cursor.getString(cursor.getColumnIndex("type"));
            BigDecimal money = new BigDecimal(cursor.getDouble(
                    cursor.getColumnIndex("money"))).setScale(2, RoundingMode.HALF_UP);
            if (type.equals("income")) {
                incomeExpenseList.get(month - 1).addIncome(money);
            } else if (type.equals("expense")) {
                incomeExpenseList.get(month - 1).addExpense(money);
            }
        }

        // summing them up
        for (int i = 0; i < 12; ++i) {
            incomeExpenseList.get(12).addExpense(incomeExpenseList.get(i).getExpense());
            incomeExpenseList.get(12).addIncome(incomeExpenseList.get(i).getIncome());
        }

        cursor.close();
        return incomeExpenseList;
    }

    private void FromListAddingToPieChart(PieChart pieChart, List<IncomeExpense> incomeExpenseList, final String type) {
        List<Integer> colorList = new ArrayList<>(Arrays.asList(0xFF437145, 0xFFCD3700, 0xFF8968CD,
                0xFFA52A2A, 0xFF999999, 0xFF8B008B, 0xFF9932CC, 0xFF2F4F4F, 0xFFFF69B4, 0xFFFF4500,
                0xFF20B2AA, 0xFF6B8E23));
        List<PieEntry> incomeOrExpenseList = new LinkedList<>();
        int size = 1;
        for (int i = 0; i < 12; ++i) {
            try {
                if (type.equals("income") && incomeExpenseList.get(i).getIncome().compareTo(new BigDecimal(0)) > 0) {
                    incomeOrExpenseList.add(new PieEntry(incomeExpenseList.get(i).getIncome().divide(incomeExpenseList.get(12).getIncome(),
                            BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).floatValue(), String.valueOf(i+1)));
                    ++size;
                } else if (type.equals("expense") && incomeExpenseList.get(i).getExpense().compareTo(new BigDecimal(0)) > 0) {
                    incomeOrExpenseList.add(new PieEntry(incomeExpenseList.get(i).getExpense().divide(incomeExpenseList.get(12).getExpense(),
                            BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).floatValue(), String.valueOf(i+1)));
                    ++size;
                }
            } catch (ArithmeticException e) {
                e.printStackTrace();
            }
        }
        PieDataSet pieDataSet = new PieDataSet(incomeOrExpenseList, type);
        pieDataSet.setColors(colorList.subList(0, size));

        PieData pieData = new PieData(pieDataSet);
        pieDataModification(pieData);
        pieChart.setData(pieData);

        setPieChartRefreshable(pieChart);

        amountIncomeText.setText(String.format(Locale.getDefault(), "%.2f", incomeExpenseList.get(12).getIncome()));
        amountExpenseText.setText(String.format(Locale.getDefault(), "%.2f", incomeExpenseList.get(12).getExpense()));
    }

    private void pieDataModification(PieData pieData) {
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(12f);
    }

    private void setPieChartRefreshable(PieChart pieChart) {
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);

        pieChart.refreshDrawableState();
        pieChart.invalidate();
    }
}