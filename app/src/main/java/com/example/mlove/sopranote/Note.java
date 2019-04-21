package com.example.mlove.sopranote;

import android.support.annotation.NonNull;

public class Note {
    private int duration;
    private String note;

    public Note(String note, int duration) {
        this.note = note;
        this.duration = duration;
    }


    public int getDuration() {
        return duration;
    }

    public void incrementDurationBy(int duration) {
        this.duration += duration;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean noteEquals(String other) {
        return other.equals(note);
    }

    public String toString() {
        return note + "(" + duration * 50 + ")   ";
    }


}
