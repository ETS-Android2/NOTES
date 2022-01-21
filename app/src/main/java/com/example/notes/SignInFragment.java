package com.example.notes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class SignInFragment extends Fragment {

    EditText loginEmailEditText;
    EditText loginPasswordEditText;
    Button loginLogInButton;
    SharedPreferences sharedPreferences;
    CheckBox loginRememberCheckBox;
    TextView loginForgotPassword;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        loginEmailEditText = fragmentView.findViewById(R.id.loginEmailEditText);
        loginPasswordEditText = fragmentView.findViewById(R.id.loginPasswordEditText);
        loginLogInButton = fragmentView.findViewById(R.id.loginLogInButton);
        loginRememberCheckBox = fragmentView.findViewById(R.id.loginRememberCheckBox);
        loginForgotPassword = fragmentView.findViewById(R.id.loginForgotPassword);
        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setMessage("Please wait.");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        loginLogInButton.setOnClickListener(view -> LogInUser());
        loginForgotPassword.setOnClickListener(view -> ForgotPassword());
        sharedPreferences = requireContext().getSharedPreferences("APP", requireContext().MODE_PRIVATE);

        String email = sharedPreferences.getString("Email", "");
        String password = sharedPreferences.getString("Password", "");
        if (!email.isEmpty() && !password.isEmpty()) {
            loginEmailEditText.setText(email);
            loginPasswordEditText.setText(password);
            LogInUser();
        }
        return fragmentView;
    }

    private void ForgotPassword() {
        Intent intent = new Intent(getContext(), ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void LogInUser() {
        String email = loginEmailEditText.getText().toString();
        String password = loginPasswordEditText.getText().toString();
        if (email.isEmpty()) {
            loginEmailEditText.setError("Email cannot be blank");
            loginEmailEditText.requestFocus();
        } else if (password.isEmpty()) {
            loginPasswordEditText.setError("Password cannot be Blank");
            loginPasswordEditText.requestFocus();
        } else {
            blockLogIn();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getUser().isEmailVerified()) {
                            if (loginRememberCheckBox.isChecked()) {
                                sharedPreferences.edit().putString("Email", email).apply();
                                sharedPreferences.edit().putString("Password", password).apply();
                            }
                            Intent intent = new Intent(requireContext(), DisplayNoteActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        } else {
                            Toast.makeText(requireContext(), "Email ID not verified", Toast.LENGTH_LONG).show();
                            allowLogIn();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(requireContext(), "Email or Password is Incorrect!", Toast.LENGTH_LONG).show();
                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(requireContext(), "Email ID not found!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                    }
                    allowLogIn();
                }
            });
        }
    }

    private void allowLogIn() {
        loginLogInButton.setEnabled(true);
        progressDialog.cancel();
    }

    private void blockLogIn() {
        loginLogInButton.setEnabled(false);
        progressDialog.show();
    }
}