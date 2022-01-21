package com.example.notes;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment {

    EditText registerNameEditText;
    EditText registerAgeEditText;
    EditText registerEmailEditText;
    EditText registerPasswordEditText;
    Button registerRegisterButton;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        registerNameEditText = fragmentView.findViewById(R.id.registerNameEditText);
        registerAgeEditText = fragmentView.findViewById(R.id.registerAgeEditText);
        registerEmailEditText = fragmentView.findViewById(R.id.registerEmailEditText);
        registerPasswordEditText = fragmentView.findViewById(R.id.registerPasswordEditText);
        registerRegisterButton = fragmentView.findViewById(R.id.registerRegisterButton);
        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setMessage("Please wait.");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        registerRegisterButton.setOnClickListener(view -> registerUser());
        return fragmentView;
    }

    private void registerUser() {
        String name = registerNameEditText.getText().toString();
        String age = registerAgeEditText.getText().toString();
        String email = registerEmailEditText.getText().toString();
        String password = registerPasswordEditText.getText().toString();

        if (name.trim().length() < 3) {
            registerNameEditText.setError("Name must have at least 3 characters");
            registerNameEditText.requestFocus();
        } else if (age.isEmpty()) {
            registerAgeEditText.setError("Age cannot be blank");
            registerAgeEditText.requestFocus();
        } else if (Integer.parseInt(age) <= 0) {
            registerAgeEditText.setError("Age cannot be zero");
            registerAgeEditText.requestFocus();
        } else if (email.trim().isEmpty()) {
            registerEmailEditText.setError("Email cannot be blank");
            registerEmailEditText.requestFocus();
        } else if (password.isEmpty()) {
            registerPasswordEditText.setError("Password cannot be blank");
            registerPasswordEditText.requestFocus();
        } else {
            blockRegister();
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        userEmailVerification();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(requireContext(), "This Email ID is already registered!", Toast.LENGTH_LONG).show();
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(requireContext(), "Please enter correct Information", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(), "Registration Failed! Code: " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                    allowRegister();
                }
            });
        }
    }

    private void userEmailVerification() {
        String name = registerNameEditText.getText().toString();
        String age = registerAgeEditText.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText(requireContext(), "Verification Email Sent!", Toast.LENGTH_LONG).show();
                FirebaseDatabase.getInstance().getReference(user.getUid()).child("Information").child("Name").setValue(name);
                FirebaseDatabase.getInstance().getReference(user.getUid()).child("Information").child("Age").setValue(age);
                allowRegister();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Something Went Wrong!", Toast.LENGTH_LONG).show();
            allowRegister();
        });

    }

    private void allowRegister() {
        registerRegisterButton.setEnabled(true);
        progressDialog.cancel();
    }

    private void blockRegister() {
        registerRegisterButton.setEnabled(false);
        progressDialog.show();
    }
}