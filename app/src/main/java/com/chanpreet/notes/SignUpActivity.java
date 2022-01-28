package com.chanpreet.notes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.chanpreet.notes.databinding.ActivitySignUpBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameET;
    private EditText emailET;
    private EditText passwordET;
    private EditText confirmPasswordET;

    private TextInputLayout nameETLayout;
    private TextInputLayout emailETLayout;
    private TextInputLayout passwordETLayout;
    private TextInputLayout confirmPasswordETLayout;
    private NoteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignUpBinding binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        binding.linearLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_up_l));

        //referencing
        nameET = binding.nameET;
        emailET = binding.emailET;
        passwordET = binding.passwordET;
        confirmPasswordET = binding.confirmPasswordET;
        nameETLayout = binding.nameETLayout;
        emailETLayout = binding.emailETLayout;
        passwordETLayout = binding.passwordETLayout;
        confirmPasswordETLayout = binding.confirmPasswordETLayout;
        database = NoteDatabase.getInstance();


        //listeners
        binding.signUpBtn.setOnClickListener(view -> createAccount());
        binding.signInBtn.setOnClickListener(view -> openSignInActivity());
        binding.privacyPolicyTV.setOnClickListener(view -> openPrivacyPolicy());
        nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateName();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
        confirmPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateConfirmPassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void createAccount() {
        validateName();
        validateEmail();
        validatePassword();
        validateConfirmPassword();

        boolean result = nameETLayout.getError() == null
                && emailETLayout.getError() == null
                && passwordETLayout.getError() == null
                && confirmPasswordETLayout.getError() == null;

        if (result) {
            String name = nameET.getText().toString().trim();
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();
            database.createAccount(this, name, email, password);
        }
    }

    private void validateName() {
        String name = nameET.getText().toString().trim();
        if (name.length() < Params.NAME_MIN_LENGTH) {
            nameETLayout.setError(getString(R.string.invalid_name_error));
        } else {
            nameETLayout.setError(null);
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

    private void validatePassword() {
        String password = passwordET.getText().toString().trim();
        boolean hasCapital = false;
        boolean hasLower = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;
        boolean result = !(password.length() <= Params.PASSWORD_MIN_LENGTH);
        for (int j = 0; j < password.length(); j++) {
            char c = password.charAt(j);
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isDigit(c)
                    && !Character.isLetter(c)
                    && !Character.isWhitespace(c)) {
                hasSpecialCharacter = true;
            }
        }
        result = hasCapital && hasLower && hasNumber && hasSpecialCharacter && result;
        if (result) {
            passwordETLayout.setError(null);
        } else {
            passwordETLayout.setError(getString(R.string.invalid_password_error));
        }
    }

    private void validateConfirmPassword() {
        String password1 = passwordET.getText().toString().trim();
        String password2 = confirmPasswordET.getText().toString().trim();
        if (password1.equals(password2)) {
            confirmPasswordETLayout.setError(null);
        } else {
            confirmPasswordETLayout.setError(getString(R.string.wrong_confirm_password_error));
        }
    }

    private void openSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void openPrivacyPolicy() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/chanpreet3000/NOTES/blob/main/policy.txt")));
    }
}