package com.example.finalprojectmoore;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private View headerView;

    public int user_id;  // Will be used to get the proper sets for the user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting up the floating button
        //fab = findViewById(R.id.fab);
        //floatingListener();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);  // Access the header

        setWelcomeMessage();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navBarSetup();
    }

    // Sets the welcome to Hello, <username>!
    private void setWelcomeMessage() {
        Bundle extras = getIntent().getExtras();
        user_id = extras.getInt(LoginActivity.USERID);
        Log.d("User ID", String.valueOf(user_id));
        String username = extras.getString(LoginActivity.USERNAME);
        TextView user_welcome = headerView.findViewById(R.id.welcome_user);
        //Log.d("User", String.valueOf(user_welcome));
        user_welcome.setText(String.format(getResources().getString(R.string.nav_header_subtitle), username));
    }

    // Setup for the side navigation bar
    private void navBarSetup() {
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_exercise, R.id.nav_graphs, R.id.nav_percentages, R.id.nav_plates)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    /*
    // On click for floating action
    private void floatingListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Sets the icon to the exercise log icon
        MenuItem icon = menu.findItem(R.id.icon_set);
        Drawable drawable = icon.getIcon();
        drawable.mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        icon.setEnabled(false);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // This will ask the user to confirm logging out
    @Override
    public void onBackPressed() {
        logoutDialog();
    }

    public void logoutDialog() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}