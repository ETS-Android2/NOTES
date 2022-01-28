package com.chanpreet.notes;

import android.graphics.Color;

import java.io.Serializable;

public class Note implements Serializable {
    private String ID;
    private String title;
    private String description;
    private int color;

    public Note() {
        this.ID = String.valueOf(System.currentTimeMillis());
        this.title = "";
        this.description = "";
        this.color = Color.RED;
    }
    
    public Note(String title, String description, int color) {
        this.ID = String.valueOf(System.currentTimeMillis());
        this.title = title;
        this.description = description;
        this.color = color;
    }

    public Note(String ID, String title, String description, int color) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.color = color;
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

    public int getColor() {
        return color;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
