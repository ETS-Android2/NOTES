package com.example.notes;

import androidx.annotation.NonNull;

public class Note {
    private final String ID;
    private final String title;
    private final String description;

    public Note(String ID, String title, String description) {
        this.ID = ID;
        this.title = title;
        this.description = description;
    }

    public String getID() {
        return ID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @NonNull
    public String toString() {
        return "ID : " + this.ID + "\n" +
                "Title : " + this.title + "\n" +
                "Description : " + this.description + "\n";
    }
}
