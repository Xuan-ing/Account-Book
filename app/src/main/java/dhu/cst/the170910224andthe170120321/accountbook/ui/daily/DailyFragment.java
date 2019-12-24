package dhu.cst.the170910224andthe170120321.accountbook.ui.daily;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.draw.ImageResDrawFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.bin.david.form.utils.DensityUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import dhu.cst.the170910224andthe170120321.accountbook.R;

public class DailyFragment extends Fragment {
    private DailyViewModel dailyViewModel;
    private Column<String> description;
    private Column<String> type;
    private Column<BigDecimal> money;
    private Column<Boolean> operation;
    private Column<String> time;
    private SQLiteDatabase sqLiteDatabase;
    private SmartTable<Item> smartTable;
    private Button searchButton;
    private ImageButton deleteButton;
    private TextView searchText;
    private List<Integer> isCheck;
    private String today;
    private int selected_num = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dailyViewModel = ViewModelProviders.of(this).get(DailyViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_daily, container, false);


       //imageView = root.findViewById(R.id.imageView);
        deleteButton = root.findViewById(R.id.delete);
        searchButton = root.findViewById(R.id.searchButton);
        searchText = root.findViewById(R.id.searchText);
        smartTable = root.findViewById(R.id.daily_table);

        componentsInitialization();
        smartTable.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if (cellInfo.row % 2 != 0) {
                    return ContextCompat.getColor(root.getContext(), R.color.grey_background);
                } else {
                    return TableConfig.INVALID_COLOR;
                }
            }

        });
        List<Item> itemList = readFromSQLiteToList(today);
        TableData<Item> tableData = readFromListToTableData(itemList);
        showAllItems(smartTable, tableData);

        ImageButton imageButtonIncome = root.findViewById(R.id.imageIncome);
        ImageButton imageButtonExpend = root.findViewById(R.id.imageExpend);
        setOnClickListeners(imageButtonExpend, imageButtonIncome);

        // automatically generated without any modification
        dailyViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // empty
            }
        });
        return root;
    }

    private void componentsInitialization() {
        today = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        isCheck = new LinkedList<>();
        description = new Column<>(getString(R.string.descriptionColumn),"description");
        type = new Column<>(getString(R.string.typeColumn),"type");
        type.setAutoMerge(false);
        money = new Column<>(getString(R.string.amountColumn),"money");
        money.setAutoCount(true);
        int size = DensityUtils.dp2px(getActivity(), 15);
        operation = new Column<>(getString(R.string.checkColumn), "operation", new ImageResDrawFormat<Boolean>(size, size) {
            @Override
            protected Context getContext() {
                return getActivity();
            }

            @Override
            protected int getResourceID(Boolean aBoolean, String value, int position) {
                if (operation.getDatas().get(position)) {
                    return R.drawable.checked;
                } else {
                    return 0;
                }
            }
        });
        operation.setComputeWidth(40);
        operation.setOnColumnItemClickListener(new OnColumnItemClickListener<Boolean>() {
            @Override
            public void onClick(Column<Boolean> column, String value, Boolean aBoolean, int position) {
                if (operation.getDatas().get(position)) {
                    operation.getDatas().set(position, false);
                    getSelection(position, false);
                    --selected_num;
                } else {
                    operation.getDatas().set(position, true);
                    getSelection(position, true);
                    ++selected_num;
                }
                if (selected_num != 0) {
                    deleteButton.setVisibility(View.VISIBLE);
                } else {
                    deleteButton.setVisibility(View.INVISIBLE);
                }
                smartTable.refreshDrawableState();
                smartTable.invalidate();
            }
        });
        time = new Column<>(getString(R.string.dateColumn),"time");

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < isCheck.size(); ++i) {
                    int position = isCheck.get(i);
                    if (operation.getDatas().get(position)) {
                        String TYPE = type.getDatas().get(position);
                        BigDecimal MONEY = money.getDatas().get(position);
                        String DESC = description.getDatas().get(position);
                        String TIME = time.getDatas().get(position);

                        sqLiteDatabase.execSQL("delete from statement where type=\'" + TYPE + "\' and " +
                                "money=\'" + MONEY + "\' and description=\'" + DESC +
                                "\' and time=\'" + TIME + "\';");
                    }
                }
                isCheck.clear();
                List<Item> items = readFromSQLiteToList(today);
                TableData<Item> tableData = readFromListToTableData(items);
                showAllItems(smartTable, tableData);
                deleteButton.setVisibility(View.INVISIBLE);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchDay = searchText.getText().toString();
                if(searchDay.matches("\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2}")) {
                    List<Item> itemList = readFromSQLiteToList(searchDay);
                    TableData<Item> tableData = readFromListToTableData(itemList);
                    showAllItems(smartTable, tableData);
                }
            }
        });

    }

    private void sqLiteDatabaseInitialization(final String pathName) {
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(pathName, null);
        sqLiteDatabase.execSQL("create table if not exists statement(time Date not null, " +
                "type varchar(10), money double not null, description varchar(1000));");
    }

    private List<Item> readFromSQLiteToList(final String dayString) {
        /*
         * database initialization
         * 'select' from database
         * write to an ArrayList using cursor
         * return ArrayList
         */
        int differenceDay = getDifferenceDay(dayString);

        sqLiteDatabaseInitialization(getActivity().getFilesDir().getPath() + "/statement.db3");

        List<Item> itemList = new ArrayList<>();
        // query for only today's transactions
        Cursor cursor = sqLiteDatabase.rawQuery("select * from statement where time=date('now', \'" + differenceDay + " days')", null);
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex("time"))
                    .replace(",", "");
            String type = cursor.getString(cursor.getColumnIndex("type"));
            BigDecimal money =
                    new BigDecimal(cursor.getDouble(cursor.getColumnIndex("money")))
                            .setScale(2, RoundingMode.HALF_UP);
            String description = cursor.getString(cursor.getColumnIndex("description"));
            itemList.add(new Item(type, money, description, false, date));
        }
        cursor.close();
        return itemList;
    }

    private TableData<Item> readFromListToTableData(final List<Item> itemList) {
        return new TableData<>(
                getString(R.string.tableName), itemList, type, money, time, description, operation);
    }

    private void showAllItems(SmartTable<Item> smartTable, TableData<Item> tableData) {
        smartTable.setTableData(tableData);
        smartTable.setZoom(false);
        smartTable.getConfig()
                .setShowXSequence(false)
                .setShowTableTitle(true)
                .setShowColumnTitle(false)
                .setContentStyle(new FontStyle(50, Color.BLACK))
                .setMinTableWidth(100);

        smartTable.refreshDrawableState();
        smartTable.invalidate();
    }

    private void setOnClickListeners(final ImageButton imageButtonExpend,
                                     final ImageButton imageButtonIncome) {
        imageButtonIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTransaction(getString(R.string.incomePrompt));
            }
        });
        imageButtonExpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTransaction(getString(R.string.expensePrompt));
            }
        });
    }

    private void insertTransaction(final String mode) {
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.insert_layout, null);

        new AlertDialog.Builder(getActivity())
                .setTitle(mode.toUpperCase())
                .setCancelable(true)
                .setView(view)
                .setPositiveButton(R.string.AlertDialog_PositiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText moneyAmountText = view.findViewById(R.id.insertAmountOfMoney);
                        if (moneyAmountText.getText().toString().isEmpty()) {
                            return;
                        }
                        BigDecimal moneyAmount = new BigDecimal(moneyAmountText.getText().toString());
                        EditText descriptionText = view.findViewById(R.id.insertDescription);
                        String description = descriptionText.getText().toString();
                        insertIntoDatabase(mode, moneyAmount, description);
                    }
                })
                .show();
    }

    private void insertIntoDatabase(String mode, BigDecimal moneyAmount, String description) {
        sqLiteDatabase.execSQL("insert into statement values (date('now'), \'"+ mode + "\', " +
                moneyAmount.doubleValue() + ", \'" + description + "\');");

        List<Item> items = readFromSQLiteToList(today);
        TableData<Item> tableData = readFromListToTableData(items);
        showAllItems(smartTable, tableData);
    }

    private void getSelection(int position, boolean selected) {
        if (position > -1) {
            if (selected && !isCheck.contains(position)) {
                isCheck.add(position);
            } else if (!selected && isCheck.contains(position)) {
                for (int i = 0; i < isCheck.size(); ++i) {
                    if (isCheck.get(i) == position) {
                        isCheck.remove(i);
                    }
                }
            }
        }
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
}