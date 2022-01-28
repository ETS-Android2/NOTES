package com.chanpreet.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;

import com.chanpreet.notes.databinding.ActivitySignInBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private EditText emailET;
    private EditText passwordET;
    private TextInputLayout emailETLayout;
    private TextInputLayout passwordETLayout;
    private CheckBox rememberCheckBox;
    private NoteDatabase database;
    ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        //Referencing
        emailET = binding.emailET;
        passwordET = binding.passwordET;
        emailETLayout = binding.emailETLayout;
        passwordETLayout = binding.passwordETLayout;
        rememberCheckBox = binding.rememberCheckBox;
        database = NoteDatabase.getInstance();

        binding.linearLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_up_l));

        //Listeners
        binding.signInBtn.setOnClickListener(view -> signIn());
        binding.signUpBtn.setOnClickListener(view -> openSignUpActivity());
        binding.forgotPasswordBtn.setOnClickListener(view -> openForgetPasswordActivity());
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
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void signIn() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        if (isValidInformation()) {
            database.signIn(this, email, password, rememberCheckBox.isChecked());
        }
    }

    private boolean isValidInformation() {
        validateEmail();
        validatePassword();
        return emailETLayout.getError() == null && passwordETLayout.getError() == null;
    }

    private void openSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void openForgetPasswordActivity() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void validateEmail() {
        String email = emailET.getText().toString().trim();
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailETLayout.setError(null);
        } else {
            emailETLayout.setError(getString(R.string.invalid_email_error));
        }
    }

    private void validatePassword() {
        String password = passwordET.getText().toString().trim();
        if (password.length() == 0) {
            passwordETLayout.setError(getString(R.string.empty_password_error));
        } else {
            passwordETLayout.setError(null);
        }
    }

    public ActivitySignInBinding getBinding() {
        return this.binding;
    }
}