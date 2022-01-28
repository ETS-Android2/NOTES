package com.chanpreet.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;


import androidx.appcompat.app.AppCompatActivity;
import com.chanpreet.notes.databinding.ActivitySplashScreenBinding;

import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        binding.signInBtn.setOnClickListener(view -> startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class)));
        binding.signUpBtn.setOnClickListener(view -> startActivity(new Intent(SplashScreenActivity.this, SignUpActivity.class)));
        binding.linearLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_up_l));

        NoteDatabase.getInstance().autoSignIn(this);
    }
}