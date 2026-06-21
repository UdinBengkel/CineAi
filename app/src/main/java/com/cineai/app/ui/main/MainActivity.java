package com.cineai.app.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cineai.app.R;
import com.cineai.app.databinding.ActivityMainBinding;
import com.cineai.app.ui.home.HomeFragment;
import com.cineai.app.ui.watchlist.WatchlistFragment;
import com.cineai.app.ui.search.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setupBottomNav();

        // Load home fragment by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), "home");
            binding.bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment(), "home");
                setTitle("CineAI");
                return true;
            } else if (id == R.id.nav_search) {
                loadFragment(new SearchFragment(), "search");
                setTitle("Cari Film");
                return true;
            } else if (id == R.id.nav_watchlist) {
                loadFragment(new WatchlistFragment(), "watchlist");
                setTitle("Watchlist");
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }
}