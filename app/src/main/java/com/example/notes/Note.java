package com.example.notes;

public class Note {
    private String ID;
    private String title;
    private String description;

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

    public String toString() {
        return "ID : " + this.ID + "\n" +
                "Title : " + this.title + "\n" +
                "Description : " + this.description + "\n";
    }
}
