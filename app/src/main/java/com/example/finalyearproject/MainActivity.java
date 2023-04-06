package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.finalyearproject.databinding.ActivityMainBinding;
import com.example.finalyearproject.fragments.ChartsFragment;
import com.example.finalyearproject.fragments.MoreFragment;
import com.example.finalyearproject.fragments.ReceiptsFragment;
import com.example.finalyearproject.fragments.RecordsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private com.example.finalyearproject.databinding.ActivityMainBinding binding;
    RecordsFragment recordsFragment = new RecordsFragment();
    ReceiptsFragment receiptsFragment = new ReceiptsFragment();
    ChartsFragment chartsFragment = new ChartsFragment();
    MoreFragment moreFragment = new MoreFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBottomNavBar();
    }

    /* set default fragment shown when launch app, and link nav bar with corresponding fragments */
    private void initBottomNavBar() {
        getSupportFragmentManager().beginTransaction()
                .replace(binding.container.getId(), recordsFragment).commit();
        binding.bottomNavBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_records) {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.container.getId(), recordsFragment).commit();
                return true;
            } else if (itemId == R.id.nav_receipts) {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.container.getId(), receiptsFragment).commit();
                return true;
            } else if (itemId == R.id.nav_charts) {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.container.getId(), chartsFragment).commit();
                return true;
            } else if (itemId == R.id.nav_more) {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.container.getId(), moreFragment).commit();
                return true;
            }
            return false;
        });
    }

}