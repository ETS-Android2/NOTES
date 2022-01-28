package com.chanpreet.notes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.chanpreet.notes.databinding.FragmentContactUsBinding;

import java.util.Objects;

public class ContactUsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentContactUsBinding binding = FragmentContactUsBinding.inflate(inflater, container, false);
        binding.contactUsBtn.setOnClickListener(view -> contactUs());
        binding.linearLayout.setAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_up_l));
        return binding.getRoot();
    }

    private void contactUs() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.contact_us_email)});
        startActivity(Intent.createChooser(emailIntent, "Send mail."));
    }
}