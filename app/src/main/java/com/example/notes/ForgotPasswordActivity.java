package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText forgotPasswordEditText;
    Button forgotPasswordButton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotPasswordEditText = findViewById(R.id.forgotPasswordEditText);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);

        forgotPasswordButton.setOnClickListener(view -> SendPasswordResetLink());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void SendPasswordResetLink() {

        String email = forgotPasswordEditText.getText().toString();

        if (!email.isEmpty()) {
            progressDialog.show();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Password Reset Link Sent!", Toast.LENGTH_LONG).show();
                    progressDialog.cancel();
                    finish();
                }
            }).addOnFailureListener(e -> {
                if (e instanceof FirebaseAuthInvalidUserException) {
                    Toast.makeText(getApplicationContext(), "Email ID does not Exists!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "Please enter a valid Email Address!", Toast.LENGTH_SHORT).show();
        }
    }
}