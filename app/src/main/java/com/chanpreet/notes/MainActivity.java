package com.chanpreet.notes;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chanpreet.notes.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private DrawerLayout drawerLayout;
    private NoteDisplayFragment noteDisplayFragment;
    private ContactUsFragment contactUsFragment;
    private YourProfileFragment yourProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        Toolbar toolbar = binding.toolbar;
        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navigationView;
        noteDisplayFragment = new NoteDisplayFragment();
        contactUsFragment = new ContactUsFragment();
        yourProfileFragment = new YourProfileFragment();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, noteDisplayFragment);
        transaction.commit();
        navigationView.setCheckedItem(R.id.nav_notes);

        TextView nav_name_TV = navigationView.getHeaderView(0).findViewById(R.id.nav_name_TV);
        TextView nav_email_TV = navigationView.getHeaderView(0).findViewById(R.id.nav_email_TV);

        NoteDatabase.getInstance().getEmailAndName(this, nav_email_TV, nav_name_TV);

        navigationView.setNavigationItemSelectedListener(item -> {
            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.nav_rate_us) {
                rateUs();
                return true;
            } else if (id == R.id.sign_out) {
                signOut();
                return true;
            } else if (id == R.id.nav_notes) {
                fragment = noteDisplayFragment;
            } else if (id == R.id.nav_account) {
                fragment = yourProfileFragment;
            } else if (id == R.id.nav_contact_us) {
                fragment = contactUsFragment;
            } else {
                return false;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            transaction1.replace(R.id.frameLayout, fragment);
            transaction1.addToBackStack(null);
            transaction1.commit();
            return true;
        });
    }

    private void rateUs() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void signOut() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        View layout = getLayoutInflater().inflate(R.layout.custom_sign_out, null);

        dialog.setView(layout);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
        ((Button) layout.findViewById(R.id.signOutBtn)).setOnClickListener(view -> {
            NoteDatabase.getInstance().signOut();
            SharedPreferences sharedPreferences = getSharedPreferences(Params.SP_NAME, MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(this, SplashScreenActivity.class));
            finish();
        });

        ((Button) layout.findViewById(R.id.closeBtn)).setOnClickListener(view -> dialog.cancel());
    }
}