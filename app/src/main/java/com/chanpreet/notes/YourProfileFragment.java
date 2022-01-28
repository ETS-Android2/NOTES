package com.chanpreet.notes;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chanpreet.notes.databinding.FragmentAccountBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class YourProfileFragment extends Fragment {

    FragmentAccountBinding binding;
    private EditText nameET;
    private TextInputLayout nameETLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);

        nameET = binding.nameET;
        nameETLayout = binding.nameETLayout;

        NoteDatabase.getInstance().getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child(Params.DB_INFORMATION).child(Params.DB_NAME).getValue(String.class);
                String email = snapshot.child(Params.DB_INFORMATION).child(Params.DB_EMAIL).getValue(String.class);
                nameET.setText(name);
                binding.emailET.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        binding.saveFAB.setOnClickListener(view -> saveProfile());
        binding.deleteAccountBtn.setOnClickListener(view -> deleteAccount());
        binding.resetPasswordBtn.setOnClickListener(view -> resetPassword());

        binding.linearLayout.setAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_up_l));
        return binding.getRoot();
    }

    private void validateName() {
        String name = nameET.getText().toString().trim();
        if (name.length() < Params.NAME_MIN_LENGTH) {
            nameETLayout.setError(getString(R.string.invalid_name_error));
        } else {
            nameETLayout.setError(null);
        }
    }

    private void saveProfile() {
        validateName();
        boolean result = nameETLayout.getError() == null;
        if (result) {
            String name = nameET.getText().toString().trim();
            NoteDatabase.getInstance().getDatabaseReference().child(Params.DB_INFORMATION).child(Params.DB_NAME).setValue(name);
            Toast.makeText(requireContext(), getString(R.string.account_profile_updated_success), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), MainActivity.class));
            requireActivity().finish();
        }
    }

    private void deleteAccount() {
        AlertDialog dialog = new AlertDialog.Builder(requireContext()).create();
        View layout = getLayoutInflater().inflate(R.layout.custom_delete_account, null);

        dialog.setView(layout);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
        ((Button) layout.findViewById(R.id.deleteAccountBtn)).setOnClickListener(view -> {
            NoteDatabase.getInstance().deleteAccount(requireContext());
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Params.SP_NAME, requireContext().MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(requireContext(), SplashScreenActivity.class));
            requireActivity().finish();
        });

        ((Button) layout.findViewById(R.id.closeBtn)).setOnClickListener(view -> dialog.cancel());
    }

    private void resetPassword() {
        String email = Objects.requireNonNull(NoteDatabase.getInstance().firebaseAuth.getCurrentUser()).getEmail();
        NoteDatabase.getInstance().requestResetPasswordLink(requireActivity(), email);
    }
}