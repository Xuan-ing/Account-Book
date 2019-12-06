package dhu.cst.the170910224andthe170120321.accountbook.ui.weekly;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dhu.cst.the170910224andthe170120321.accountbook.R;

public class WeeklyFragment extends Fragment {

    private WeeklyViewModel weeklyViewModel;
    private SQLiteDatabase sqLiteDatabase;
    private BarChart barChart;
    private Button startButton;
    private EditText startText;
    private TextView show;
    private String today;
    private  XAxis xAxis;
    ArrayList<String> date = new ArrayList<>();
    BigDecimal total[] = new BigDecimal[2];

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        weeklyViewModel =
                ViewModelProviders.of(this).get(WeeklyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_weekly, container, false);

        startButton = root.findViewById(R.id.startButton);
        startText = root.findViewById(R.id.startText);
        barChart = root.findViewById(R.id.BarChart);
        show = root.findViewById(R.id.show);
        componentsInitialization();

        weeklyViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    private void componentsInitialization() {
        today = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dayString = startText.getText().toString();
                if(dayString.matches("\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2}")) {
                    MakeBarChart(dayString);
                }
            }
        });
//        Description description =new Description();
//        description.setText("过去7天收支情况分布图");
//        description.setTextColor(Color.GRAY);
//        description.setTextSize(30f);
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.animateXY(2000,2000);


        xAxis = barChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        MakeBarChart(today);
    }
    private   String getPastDate(int past,String dayString) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try{
             date = dateFormat.parse(dayString);
        }catch (ParseException e){
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - past);
        Date today = calendar.getTime();
        String result = dateFormat.format(today);
        return result;
    }//拿到任意天的日期

    private void sqLiteDatabaseInitialization(final String pathName) {
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(pathName, null);
        sqLiteDatabase.execSQL("create table if not exists statement(time Date not null, " +
                "type varchar(10), money double not null, description varchar(1000));");
    }

    private int getDifferenceDay(String dayString) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int day = 0;
        try {
            Date date1 = dateFormat.parse(dayString);
            Date date2 = dateFormat.parse(today);

            long time1 = date1.getTime();
            long time2 = date2.getTime();

            long time = time1 - time2;
            day = (int) (time / (1000 * 60 * 60 * 24));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;

    }

    private void readFromSQLiteToInt(final String dayString) {
        total[0] = new BigDecimal(0);
        total[1] = new BigDecimal(0);
        sqLiteDatabaseInitialization(getActivity().getFilesDir().getPath() + "/statement.db3");
        int differenceDay = getDifferenceDay(dayString);

        Cursor cursor = sqLiteDatabase.rawQuery("select * from statement where time=date('now', \'" + differenceDay + " days')", null);
        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndex("type"));
            BigDecimal money =
                    new BigDecimal(cursor.getDouble(cursor.getColumnIndex("money")))
                            .setScale(2, RoundingMode.HALF_UP);
            Log.d("money",money.toString());
           if(type.equals("income")) {
               total[0]=total[0].add(money);
           }else{
               total[1]=total[1].add(money);
           }

        }
        cursor.close();
    }

    private void MakeBarChart(final String dayString){

        ArrayList<BarEntry> income = new ArrayList<>();
        ArrayList<BarEntry> expend = new ArrayList<>();
        date.clear();

        for(int i=0; i<31; ++i) {
            String day = getPastDate(i,dayString);
            date.add(day);
            readFromSQLiteToInt(day);
            BarEntry incomeEntry = new BarEntry(i,total[0].floatValue());
            income.add(incomeEntry);
            BarEntry expendEntry = new BarEntry(i,total[1].floatValue());
            expend.add(expendEntry);
        }

        BarDataSet incomeDataSet = new BarDataSet(income,"每日收入");
        incomeDataSet.setColor(0xFF38E8FC);
        BarDataSet expendDataSet = new BarDataSet(expend,"每日支出");
        expendDataSet.setColor(0xFFFC7C5C);

        ArrayList<IBarDataSet> iBarDataSets = new ArrayList<>();
        iBarDataSets.add(incomeDataSet);
        iBarDataSets.add(expendDataSet);
        BarData barData = new BarData(iBarDataSets);
        barData.setBarWidth(0.4f);

        barChart.setData(barData);
        barChart.groupBars(0f,0.08f,0.06f);

        xAxis.setAxisMaximum(31f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String str = getPastDate((int)value,dayString);

                return str.substring(2) ;
            }
        });
        barChart.setVisibleXRange(0,7);
        barChart.invalidate();


    }

}