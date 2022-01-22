package com.example.notes;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavBar);
        frameLayout = findViewById(R.id.frameLayout);

        OpenFragment(new SignInFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.signInBottomNavBar) {
                fragment = new SignInFragment();

            } else if (item.getItemId() == R.id.signUpBottomNavBar) {
                fragment = new SignUpFragment();
            }
            OpenFragment(fragment);
            return true;
        });
    }

    private void OpenFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(frameLayout.getId(), fragment)
                .commit();
    }
}