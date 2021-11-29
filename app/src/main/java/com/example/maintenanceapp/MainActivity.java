package com.example.maintenanceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import com.example.maintenanceapp.Fragments.HomeFragment;
import com.example.maintenanceapp.Fragments.ProfileFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import models.Message;

public class MainActivity extends AppCompatActivity {

    public static  BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // TODO : pass subscription to profiel to unsubsribe once logged out
        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            // Navigate to selected fragment
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                    default:
                        fragment = new HomeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commitAllowingStateLoss();
                return true;
            }

        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);

    }
}