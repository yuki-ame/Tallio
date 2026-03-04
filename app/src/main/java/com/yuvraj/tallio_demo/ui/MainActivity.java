package com.yuvraj.tallio_demo.ui;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.yuvraj.tallio_demo.R;

/**
 * MainActivity.java - THE CONTAINER ACTIVITY
 *
 * This activity hosts a BottomNavigationView and swaps between
 * 4 Fragments based on which tab the user taps.
 *
 * Fragments are like mini-screens that live inside an Activity.
 * We use FragmentManager to show/hide them.
 *
 * Tabs:
 * 1. Dashboard  - spending summary + pie chart
 * 2. History    - list of all transactions with search + delete
 * 3. Add        - manually add a cash transaction
 * 4. Budget     - set monthly budget
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Show Dashboard fragment by default when app opens
        loadFragment(new DashboardFragment());

        // Handle bottom nav tab clicks
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
            } else {
                selectedFragment = new DashboardFragment();
            }

            loadFragment(selectedFragment);
            return true;
        });
    }

    // Helper method to replace the current fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}