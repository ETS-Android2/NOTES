package com.chanpreet.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.chanpreet.notes.databinding.ActivityForgotPasswordBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    private EditText emailET;
    private TextInputLayout emailETLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        emailET = binding.emailET;
        emailETLayout = binding.emailETLayout;

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.resetBtn.setOnClickListener(view -> SendPasswordResetLink());
        binding.signInBtn.setOnClickListener(view -> startActivity(new Intent(this, SignInActivity.class)));
        binding.signUpBtn.setOnClickListener(view -> startActivity(new Intent(this, SignUpActivity.class)));

        binding.linearLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_up_l));

        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void SendPasswordResetLink() {
        validateEmail();
        boolean result = emailETLayout.getError() == null;
        if (result) {
            NoteDatabase.getInstance().requestResetPasswordLink(this, emailET.getText().toString().trim());
        }
    }

    private void validateEmail() {
        String email = emailET.getText().toString().trim();
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailETLayout.setError(null);
        } else {
            emailETLayout.setError(getString(R.string.invalid_email_error));
        }
    }
}