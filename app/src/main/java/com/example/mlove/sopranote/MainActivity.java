package com.example.mlove.sopranote;


import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class MainActivity extends AppCompatActivity implements TempoInputDialog.TempoInputListener {
    private TextView noteTextImage, pitchNumberInHertz;
    private ImageView A, B, C, D, E, F, G, A2, B2, C2, D2, E2, F2, G2, A3, B3, C3, D3, E3, F3, G3, A4, B4, C4, D4, E4, F4, G4;

    private ImageView Asharp1, Csharp1, Dsharp1, Fsharp1, Gsharp1, Asharp2, Csharp2, Dsharp2, Fsharp2, Gsharp2, Asharp3, Csharp3, Dsharp3, Fsharp3, Gsharp3, Asharp4, Csharp4, Dsharp4, Fsharp4, Gsharp4;
    private final String[] trueStringNoteValues = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    private ImageView[] noteImageViews;
    private ImageView[] secondNoteImages;
    private ImageView[] thirdNoteImages;
    private ImageView[] fourthNoteImages;

    private ImageView currentDisplayedNote;

    private Button tempoInputButton;
    private Button tempoStopButton;
    private MediaPlayer tempoPlayer;
    private Handler tempoHandler;
    private TextView TempoView;
    private Runnable tempoRunner;
    private int interval;


    private ArrayList<String> listOfAllStringNotes;
    private Button writeArray;
    private Button stopWriting;
    private boolean shouldWrite;
    private TextView noteDisplay;

    private static final String[] notes = new String[10001];
    private static double range = 1.225;
    private static int cursor = 7;
    private static double frontIndex = 41;
    private static double middleIndex = 41;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        A = findViewById(R.id.a1);
        B = findViewById(R.id.b1);
        C = findViewById(R.id.c1);
        D = findViewById(R.id.d1);
        E = findViewById(R.id.e1);
        F = findViewById(R.id.f1);
        G = findViewById(R.id.g1);
        A2 = findViewById(R.id.a2);
        B2 = findViewById(R.id.b2);
        C2 = findViewById(R.id.c2);
        D2 = findViewById(R.id.d2);
        E2 = findViewById(R.id.e2);
        F2 = findViewById(R.id.f2);
        G2 = findViewById(R.id.g2);
        A3 = findViewById(R.id.a3);
        B3 = findViewById(R.id.b3);
        C3 = findViewById(R.id.c3);
        D3 = findViewById(R.id.d3);
        E3 = findViewById(R.id.e3);
        F3 = findViewById(R.id.f3);
        G3 = findViewById(R.id.g3);
        A4 = findViewById(R.id.a4);
        B4 = findViewById(R.id.b4);
        C4 = findViewById(R.id.c4);
        D4 = findViewById(R.id.d4);
        E4 = findViewById(R.id.e4);
        F4 = findViewById(R.id.f4);
        G4 = findViewById(R.id.g4);
        Asharp1 = findViewById(R.id.a_sharp1);
        Csharp1 = findViewById(R.id.c_sharp1);
        Dsharp1 = findViewById(R.id.d_sharp1);
        Fsharp1 = findViewById(R.id.f_sharp1);
        Gsharp1 = findViewById(R.id.g_sharp1);
        Asharp2 = findViewById(R.id.a_sharp2);
        Csharp2 = findViewById(R.id.c_sharp2);
        Dsharp2 = findViewById(R.id.d_sharp2);
        Fsharp2 = findViewById(R.id.f_sharp2);
        Gsharp2 = findViewById(R.id.g_sharp2);
        Asharp3 = findViewById(R.id.a_sharp3);
        Csharp3 = findViewById(R.id.c_sharp3);
        Dsharp3 = findViewById(R.id.d_sharp3);
        Fsharp3 = findViewById(R.id.f_sharp3);
        Gsharp3 = findViewById(R.id.g_sharp3);
        Asharp4 = findViewById(R.id.a_sharp4);
        Csharp4 = findViewById(R.id.c_sharp4);
        Dsharp4 = findViewById(R.id.d_sharp4);
        Fsharp4 = findViewById(R.id.f_sharp4);
        Gsharp4 = findViewById(R.id.g_sharp4);

        pitchNumberInHertz = findViewById(R.id.txtFrequency);
        noteTextImage = findViewById(R.id.txtNote);
        tempoInputButton = findViewById(R.id.TempoInputButton);
        TempoView = findViewById(R.id.tempo);
        tempoStopButton = findViewById(R.id.tempoStopButton);
        tempoPlayer = MediaPlayer.create(this, R.raw.metronome_beep2);

        openTempoDialog();
        tempoHandler = new Handler();
        tempoRunner = new Runnable() {
            @Override
            public void run() {
                tempoPlayer.start();
                tempoHandler.postDelayed(this, interval);
            }
        };

        noteImageViews = new ImageView[]{A, Asharp1, B, C, Csharp1, D, Dsharp1, E, F, Fsharp1, G, Gsharp1};
        secondNoteImages = new ImageView[]{A2, Asharp2, B2, C2, Csharp2, D2, Dsharp2, E2, F2, Fsharp2, G2, Gsharp2};
        thirdNoteImages = new ImageView[]{A3, Asharp3, B3, C3, Csharp3, D3, Dsharp3, E3, F3, Fsharp3, G3, Gsharp3};
        fourthNoteImages = new ImageView[]{A4, Asharp4, B4, C4, Csharp4, D4, Dsharp4, E4, F4, Fsharp4, G4, Gsharp4};
        currentDisplayedNote = noteImageViews[0];
        clearImages();
        shiftPitches(trueStringNoteValues);

        listOfAllStringNotes = new ArrayList<>(10000);
        writeArray = findViewById(R.id.WriteArrayButton);
        stopWriting = findViewById(R.id.StopRecording);
        shouldWrite = false;
        noteDisplay = findViewById(R.id.displayNotes);


        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                final float pitchInHz = result.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processPitch(pitchInHz);
                    }
                });
            }
        };
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(p);


        new Thread(dispatcher,"Audio Dispatcher").start();

        stopWriting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldWrite = false;
                filterMelodyList();
            }
        });

        writeArray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listOfAllStringNotes.clear();
                shouldWrite = true;
            }
        });

        tempoInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTempoDialog();
            }
        });

        tempoStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempoHandler.removeCallbacks(tempoRunner);
            }
        });
    }

    private void openTempoDialog() {
        TempoInputDialog  tempoInputDialog = new TempoInputDialog();
        tempoInputDialog.show(getSupportFragmentManager(), "tempo input dialog");
    }

    private void processPitch(float pitchInHz) {
        if (pitchInHz != -1.0) {
            pitchNumberInHertz.setText(String.format("%.2f", pitchInHz));
            noteTextImage.setText(notes[(int) pitchInHz]);
            if (shouldWrite) {
                listOfAllStringNotes.add(notes[(int) pitchInHz]);
            }
        } else if (shouldWrite) {
            listOfAllStringNotes.add("Rest");
        }
    }

    private void filterMelodyList() {
        final String TAG = "filtering the notes";
        noteDisplay.setText("");
        Note tempNote = new Note("", 0);

        int startIndex = 0;
        while(startIndex < listOfAllStringNotes.size() && listOfAllStringNotes.get(startIndex).equals("Rest")) {
            startIndex++;
        }
        ArrayList<Note> notesWithDurations = new ArrayList<>();
        for (int i = startIndex; i < listOfAllStringNotes.size() - 1; i++) {
            Log.i(TAG, "Note at time " + i * 50 + ": " + listOfAllStringNotes.get(i));
            if (!tempNote.equals(listOfAllStringNotes.get(i))) {
                tempNote = new Note(listOfAllStringNotes.get(i), 0);
                while(i + tempNote.getDuration() < listOfAllStringNotes.size()
                        && tempNote.noteEquals(listOfAllStringNotes.get(i + tempNote.getDuration()))) {
                    tempNote.incrementDurationBy(1);
                }
                if (tempNote.getDuration() > 2) {
                    notesWithDurations.add(tempNote);
                }
                i += tempNote.getDuration();
            }
        }
        for (int i = 0; i < notesWithDurations.size() - 1; i++) {
            if (notesWithDurations.get(i).noteEquals(notesWithDurations.get(i + 1).getNote())) {
                notesWithDurations.get(i + 1).incrementDurationBy(notesWithDurations.get(i).getDuration());
                notesWithDurations.remove(i);
            }
            noteDisplay.append(notesWithDurations.get(i).toString());
        }
        displayAllNotes(notesWithDurations);
    }

    public void displayAllNotes(List<Note> noteDisplayList) {
        clearImages();
        if (!noteDisplayList.isEmpty()) {
            boolean wroteFirst = false, wroteSecond = false, wroteThird = false, wroteFourth = false;
            ImageView firstTempImage = null;
            ImageView secondTempImage = null;
            ImageView thirdTempImage = null;

            ImageView fourthTempImage = null;
            Note firstNote = new Note("Rest", 0),
                    secondNote = new Note("Rest", 0),
                    thirdNote = new Note("Rest", 0),
                    fourthNote = new Note("Rest", 0);
            int noteDisplayListIndex = 0;
            if (noteDisplayList.get(0).noteEquals("Rest")) {
                noteDisplayListIndex++;
            }

            while (noteDisplayListIndex < noteDisplayList.size() && (!wroteFirst || !wroteSecond || !wroteThird || !wroteFourth)) {
                if (!wroteFirst && !noteDisplayList.get(noteDisplayListIndex).noteEquals("Rest")) {
                    wroteFirst = true;
                    calculateQuaverType(noteDisplayList.get(noteDisplayListIndex));
                    firstNote = noteDisplayList.get(noteDisplayListIndex);
                } else if (!wroteSecond && !noteDisplayList.get(noteDisplayListIndex).noteEquals("Rest")) {
                    wroteSecond = true;
                    calculateQuaverType(noteDisplayList.get(noteDisplayListIndex));
                    secondNote = noteDisplayList.get(noteDisplayListIndex);
                } else if (!wroteThird && !noteDisplayList.get(noteDisplayListIndex).noteEquals("Rest")) {
                    wroteThird = true;
                    calculateQuaverType(noteDisplayList.get(noteDisplayListIndex));
                    thirdNote = noteDisplayList.get(noteDisplayListIndex);
                } else if (!wroteFourth && !noteDisplayList.get(noteDisplayListIndex).noteEquals("Rest")) {
                    wroteFourth = true;
                    calculateQuaverType(noteDisplayList.get(noteDisplayListIndex));
                    fourthNote = noteDisplayList.get(noteDisplayListIndex);
                }
                noteDisplayListIndex++;
            }
            for (int i = 0; i < secondNoteImages.length; i++) {
                if (firstNote.getNote().equals(trueStringNoteValues[i])) {
                    Log.d("checking note", "firstNote = " + firstNote.getNote() + " trueStringVal " + trueStringNoteValues[i]);
                    if (firstTempImage != noteImageViews[i]) {
                        if (firstTempImage != null) {
                            firstTempImage.setVisibility(View.INVISIBLE);
                        }
                        if (isSharp(trueStringNoteValues[i])) {
                            if (firstNote.forwards()) {
                                noteImageViews[i].setImageResource(R.drawable.quarter_note_sharp);
                            } else {
                                noteImageViews[i].setImageResource(R.drawable.backwards_quarter_note_sharp);
                            }
                        } else {
                            if (firstNote.forwards()) {
                                if (firstNote.getQuaverType().equals("Eighth")) {
                                    noteImageViews[i].setImageResource(R.drawable.eighth_note);
                                } else {
                                    noteImageViews[i].setImageResource(R.drawable.quarter_note);
                                }
                            } else {
                                if (firstNote.getQuaverType().equals("Eighth")) {
                                    noteImageViews[i].setImageResource(R.drawable.backwards_eighth_note);
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) noteImageViews[i].getLayoutParams();
                                    layoutParams.verticalBias += 0.030;
                                    noteImageViews[i].setLayoutParams(layoutParams);
                                } else {
                                    noteImageViews[i].setImageResource(R.drawable.backwards_note);
                                }
                            }
                        }
                        noteImageViews[i].setVisibility(View.VISIBLE);
                        firstTempImage = noteImageViews[i];
                    }
                }
                if (secondNote.getNote().equals(trueStringNoteValues[i])) {
                    Log.d("checking note", "secondNote = " + secondNote.getNote() + " trueStringVal " + trueStringNoteValues[i]);

                    if (secondTempImage != secondNoteImages[i]) {
                        if (secondTempImage != null) {
                            secondTempImage.setVisibility(View.INVISIBLE);
                        }
                        if (isSharp(trueStringNoteValues[i])) {
                            if (secondNote.forwards()) {
                                noteImageViews[i].setImageResource(R.drawable.quarter_note_sharp);
                            } else {
                                noteImageViews[i].setImageResource(R.drawable.backwards_quarter_note_sharp);
                            }
                        } else {
                            if (secondNote.forwards()) {
                                if (secondNote.getQuaverType().equals("Eighth")) {
                                    secondNoteImages[i].setImageResource(R.drawable.eighth_note);
                                } else {
                                    secondNoteImages[i].setImageResource(R.drawable.quarter_note);
                                }
                            } else {
                                if (secondNote.getQuaverType().equals("Eighth")) {
                                    secondNoteImages[i].setImageResource(R.drawable.backwards_eighth_note);
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) secondNoteImages[i].getLayoutParams();
                                    layoutParams.verticalBias += 0.030;
                                    secondNoteImages[i].setLayoutParams(layoutParams);
                                } else {
                                    secondNoteImages[i].setImageResource(R.drawable.backwards_note);
                                }
                            }
                        }
                        secondNoteImages[i].setVisibility(View.VISIBLE);
                        secondTempImage = noteImageViews[i];
                    }
                }
                if (thirdNote.getNote().equals(trueStringNoteValues[i])) {
                    Log.d("checking note", "thirdNote = " + thirdNote.getNote() + " trueStringVal " + trueStringNoteValues[i]);
                    if (thirdTempImage != thirdNoteImages[i]) {
                        if (thirdTempImage != null) {
                            thirdTempImage.setVisibility(View.INVISIBLE);
                        }
                        if (isSharp(trueStringNoteValues[i])) {
                            if (thirdNote.forwards()) {
                                noteImageViews[i].setImageResource(R.drawable.quarter_note_sharp);
                            } else {
                                noteImageViews[i].setImageResource(R.drawable.backwards_quarter_note_sharp);
                            }
                        } else {
                            if (thirdNote.forwards()) {
                                if (thirdNote.getQuaverType().equals("Eighth")) {
                                    thirdNoteImages[i].setImageResource(R.drawable.eighth_note);
                                } else {
                                    thirdNoteImages[i].setImageResource(R.drawable.quarter_note);
                                }
                            } else {
                                if (thirdNote.getQuaverType().equals("Eighth")) {
                                    thirdNoteImages[i].setImageResource(R.drawable.backwards_eighth_note);
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) thirdNoteImages[i].getLayoutParams();
                                    layoutParams.verticalBias += 0.030;
                                    thirdNoteImages[i].setLayoutParams(layoutParams);
                                } else {
                                    thirdNoteImages[i].setImageResource(R.drawable.backwards_note);
                                }
                            }
                        }
                        thirdNoteImages[i].setVisibility(View.VISIBLE);
                        thirdTempImage = thirdNoteImages[i];
                        }
                    }
                if (fourthNote.getNote().equals(trueStringNoteValues[i])) {
                    Log.d("checking note", "fourthNote = " + fourthNote.getNote() + " trueStringVal " + trueStringNoteValues[i]);
                    if (fourthTempImage != fourthNoteImages[i]) {
                        if (fourthTempImage != null) {
                            fourthTempImage.setVisibility(View.INVISIBLE);
                        }
                        if (isSharp(trueStringNoteValues[i])) {
                            if (fourthNote.forwards()) {
                                noteImageViews[i].setImageResource(R.drawable.quarter_note_sharp);
                            } else {
                                noteImageViews[i].setImageResource(R.drawable.backwards_quarter_note_sharp);
                            }
                        } else {
                            if (fourthNote.forwards()) {
                                if (fourthNote.getQuaverType().equals("Eighth")) {
                                    fourthNoteImages[i].setImageResource(R.drawable.eighth_note);
                                } else {
                                    fourthNoteImages[i].setImageResource(R.drawable.quarter_note);
                                }
                            } else {
                                if (fourthNote.getQuaverType().equals("Eighth")) {
                                    fourthNoteImages[i].setImageResource(R.drawable.backwards_eighth_note);
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) fourthNoteImages[i].getLayoutParams();
                                    layoutParams.verticalBias += 0.030;
                                    fourthNoteImages[i].setLayoutParams(layoutParams);
                                } else {
                                    fourthNoteImages[i].setImageResource(R.drawable.backwards_note);
                                }
                            }
                        }
                        fourthNoteImages[i].setVisibility(View.VISIBLE);
                        fourthTempImage = fourthNoteImages[i];
                    }
                }
            }
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) A.getLayoutParams();
            ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) noteImageViews[0].getLayoutParams();
            Log.d("note stuff", "firstNote");
            Log.d("print first note", "firstNote: " + firstNote.getNote());
            Log.d("print duration", "firstNote: " + firstNote.getDuration());
            Log.d("print second note", "secondNote: " + secondNote.getNote());
        }
    }

    private void clearImages() {
        for (int i = 0; i < noteImageViews.length; i++) {
            noteImageViews[i].setVisibility(View.GONE);
            secondNoteImages[i].setVisibility(View.GONE);
            thirdNoteImages[i].setVisibility(View.GONE);
            fourthNoteImages[i].setVisibility(View.GONE);
        }
    }


    @Override
    public void setTempo(final String tempo) {
        if (!tempo.equals("")) {
            interval = 60000 / Integer.parseInt(tempo);
            TempoView.setText(tempo + " BPM");
            tempoHandler.post(tempoRunner);
        }
    }

    /*
    This algorithm uses the equal tempered tuning system to determine when a note corresponds to
    a certain frequency. Each note's frequencies are exactly the frequency of the note before it
    times the 12th root of 2. Since there are 12 possible notes in an octave, A4 will be exactly
    double the frequency of A3.
     */
    private void shiftPitches(String[] trueStringNoteValues) {
        while (frontIndex < notes.length) {
            while (middleIndex < frontIndex + range) {
                if (middleIndex < notes.length) {
                    notes[(int) middleIndex] =  trueStringNoteValues[cursor % 12];
                }
                middleIndex++;
            }
            cursor++;
            frontIndex += range * 2;
            nextRange();
        }
    }

    private static void nextRange() {
        range *= Math.cbrt(Math.sqrt(Math.sqrt(2)));
    }

    private void calculateQuaverType(Note note) {
        double doubleInterval = (double) interval;
        int duration = note.getDuration();
        String quaverType = "Quarter";
        if (duration * 50 < (doubleInterval * .6)) {
            quaverType = "Eighth";
        }
        note.setQuaverType(quaverType);
    }

    private boolean isSharp(String note) {
        return note.length() > 1;
    }

    private int getSharpIndex(char firstNoteChar) {
        switch (firstNoteChar){
            case 'A':
                return 0;
            case 'C':
                return 1;
            case 'D':
                return 2;
            case 'F':
                return 3;
            case 'G':
                return 4;
            default:
                return -1;
        }
    }

}

