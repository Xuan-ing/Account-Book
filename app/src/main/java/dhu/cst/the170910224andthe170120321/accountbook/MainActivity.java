package dhu.cst.the170910224andthe170120321.accountbook;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    // the following two are used in OptionsMenu as ItemIds
    final private static int ADD_INCOME = 0;
    final private static int ADD_EXPEND = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_daily, R.id.navigation_weekly, R.id.navigation_monthly)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, ADD_INCOME, Menu.NONE, R.string.OptionsMenu_SignUp);
        menu.add(Menu.NONE, ADD_EXPEND, Menu.NONE, R.string.OptionsMenu_SignIn);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // the implementation is still blank
        switch (item.getItemId()) {
            case ADD_INCOME:
                // a new Activity, or an AlertDialog, which can
                // transfer the USERNAME and PASSWORD to the target database
                break;
            case ADD_EXPEND:
                // a non-cancellable AlertDialog, when clicking SIGN_IN,
                // check if the pair is valid from the corresponding database
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
