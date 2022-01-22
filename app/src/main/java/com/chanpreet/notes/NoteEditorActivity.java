package com.chanpreet.notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteEditorActivity extends AppCompatActivity {

    FloatingActionButton confirmFAB;
    FloatingActionButton deleteFAB;
    DisplayNoteActivity displayNoteActivity;
    EditText titleEditText;
    EditText descriptionEditText;
    Note currentNote;
    boolean isNewNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        confirmFAB = findViewById(R.id.confirmFAB);
        deleteFAB = findViewById(R.id.deleteFAB);
        titleEditText = findViewById(R.id.titleET);
        descriptionEditText = findViewById(R.id.descriptionET);
        displayNoteActivity = new DisplayNoteActivity();

        confirmFAB.setOnClickListener(view -> UpdateNote());
        deleteFAB.setOnClickListener(view -> DeleteNote());
        getNotePassed();
        if (currentNote.getTitle().isEmpty()) {
            deleteFAB.setVisibility(View.GONE);
            isNewNote = true;
        } else {
            fillNoteInformation();
        }
    }

    private void getNotePassed() {
        Intent intent = getIntent();
        String ID = intent.getExtras().getString(Params.KEY_ID);
        String title = intent.getExtras().getString(Params.KEY_TITLE);
        String description = intent.getExtras().getString(Params.KEY_DESCRIPTION);
        currentNote = new Note(ID, title, description);
    }

    private void fillNoteInformation() {
        titleEditText.setText(currentNote.getTitle());
        descriptionEditText.setText(currentNote.getDescription());
        titleEditText.requestFocus();
    }

    private void DeleteNote() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.red_warning_icon)
                .setTitle("Delete this Note?")
                .setMessage("Following action cannot be undone!")
                .setNegativeButton("Yes", (dialogInterface, i) -> {
                    displayNoteActivity.deleteRecord(currentNote.getID());
                    finish();
                })
                .setPositiveButton("No", null)
                .show();
    }

    private void UpdateNote() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, "Title cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isNewNote) {
            displayNoteActivity.addRecord(title, description);
        } else {
            displayNoteActivity.updateRecord(currentNote.getID(), title, description);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isNewNote) {
            super.onBackPressed();
            return;
        }
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        Note tempNote = new Note(currentNote.getID(), title, description);
        if (tempNote.toString().equals(currentNote.toString())) {
            super.onBackPressed();
            return;
        }

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.yellow_warning_icon)
                .setTitle("Unsaved progress will be lost!")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton("Yes", (dialogInterface, i) -> NoteEditorActivity.super.onBackPressed())
                .setPositiveButton("No", null)
                .show();
    }
}