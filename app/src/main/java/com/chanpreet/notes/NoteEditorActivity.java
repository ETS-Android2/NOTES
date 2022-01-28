package com.chanpreet.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;


import com.chanpreet.notes.databinding.ActivityNoteEditorBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.util.Objects;

public class NoteEditorActivity extends AppCompatActivity {

    private ActivityNoteEditorBinding binding;

    public static final String EXTRA_NOTE = "EXTRA_NOTE";
    private TextInputLayout titleETLayout;
    private TextInputLayout descriptionETLayout;
    private TextInputEditText titleET;
    private TextInputEditText descriptionET;
    private Note currentNote = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(this.getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);

        titleETLayout = binding.titleETLayout;
        descriptionETLayout = binding.descriptionETLayout;
        titleET = binding.titleET;
        descriptionET = binding.descriptionET;

        //Initialize view
        initializeView();

        //Listeners
        binding.noteColorBtn.setOnClickListener(view -> {
            int color = binding.previewNote.cardLayout.getBackgroundTintList().getDefaultColor();
            ColorPicker cp = new ColorPicker(NoteEditorActivity.this, Color.red(color), Color.green(color), Color.blue(color));
            cp.show();
            Button okColor = cp.findViewById(R.id.okColorButton);
            okColor.setOnClickListener(v -> {
                binding.previewNote.cardLayout.setBackgroundTintList(ColorStateList.valueOf(cp.getColor()));
                cp.dismiss();
                updateCurrentNote();
            });
        });
        titleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateTitle();
                updateCurrentNote();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        descriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateDescription();
                updateCurrentNote();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.saveFAB.setOnClickListener(view -> saveNote());
    }

    private void saveNote() {
        validateTitle();
        validateDescription();
        boolean result = titleETLayout.getError() == null
                && descriptionETLayout.getError() == null;

        if (result) {
            NoteDatabase.getInstance().insertOrUpdate(this, currentNote);
            finish();
        }
    }

    private void initializeView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setTitle(getString(R.string.note_editor_title));
            currentNote = (Note) bundle.getSerializable(EXTRA_NOTE);
            titleET.setText(currentNote.getTitle());
            descriptionET.setText(currentNote.getDescription());
        } else {
            setTitle(getString(R.string.add_note_title));
            currentNote = new Note();
        }
        updatePreviewCard();
    }

    private void updateCurrentNote() {
        currentNote.setTitle(titleET.getText().toString());
        currentNote.setDescription(descriptionET.getText().toString());
        currentNote.setColor(binding.previewNote.cardLayout.getBackgroundTintList().getDefaultColor());
        updatePreviewCard();
    }

    private void updatePreviewCard() {
        binding.previewNote.titleTV.setText(currentNote.getTitle());
        binding.previewNote.descriptionTV.setText(currentNote.getDescription());
        binding.previewNote.cardLayout.setBackgroundTintList(ColorStateList.valueOf(currentNote.getColor()));
    }

    private void validateTitle() {
        String title = Objects.requireNonNull(titleET.getText()).toString().trim();
        if (title.isEmpty()) {
            titleETLayout.setError(getString(R.string.invalid_title_error));
        } else {
            titleETLayout.setError(null);
        }
    }

    private void validateDescription() {
        String description = Objects.requireNonNull(descriptionET.getText()).toString().trim();
        if (description.isEmpty()) {
            descriptionETLayout.setError(getString(R.string.invalid_description_error));
        } else {
            descriptionETLayout.setError(null);
        }
    }
}