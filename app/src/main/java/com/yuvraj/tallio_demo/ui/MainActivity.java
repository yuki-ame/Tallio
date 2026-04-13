package com.yuvraj.tallio_demo.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.yuvraj.tallio_demo.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNav;
    private SwitchCompat darkModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 🌓 1. Check Theme Preference BEFORE loading content
        SharedPreferences prefs = getSharedPreferences("TallioPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("isDarkMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔹 Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 🔹 Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 🌓 2. Setup Dark Mode Switch in Drawer
        MenuItem darkModeItem = navigationView.getMenu().findItem(R.id.nav_dark_mode);
        darkModeSwitch = (SwitchCompat) darkModeItem.getActionView();

        // Sync switch state with saved preference
        boolean isDark = prefs.getBoolean("isDarkMode", false);
        darkModeSwitch.setChecked(isDark);

        // Handle the Switch toggle
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                prefs.edit().putBoolean("isDarkMode", true).apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                prefs.edit().putBoolean("isDarkMode", false).apply();
            }
        });

        // 🔹 Bottom Navigation setup
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (itemId == R.id.nav_history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.nav_add) {
                selectedFragment = new AddTransactionFragment();
            } else if (itemId == R.id.nav_budget) {
                selectedFragment = new BudgetFragment();
            } else if (itemId == R.id.nav_ai) {
                selectedFragment = new ChatbotFragment();
            } else {
                selectedFragment = new DashboardFragment();
            }

            loadFragment(selectedFragment);
            return true;
        });

        // 🔹 Default screen
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
            bottomNav.setSelectedItemId(R.id.nav_dashboard);
        }
    }

    // 🔹 Drawer menu clicks
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            loadFragment(new HistoryFragment());
            bottomNav.setSelectedItemId(R.id.nav_history);
        } else if (id == R.id.nav_add) {
            loadFragment(new AddTransactionFragment());
            bottomNav.setSelectedItemId(R.id.nav_add);
        } else if (id == R.id.nav_dark_mode) {
            // 🌓 Toggle the switch manually when the row is clicked
            if (darkModeSwitch != null) {
                darkModeSwitch.setChecked(!darkModeSwitch.isChecked());
            }
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // 🔹 Fragment loader
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}