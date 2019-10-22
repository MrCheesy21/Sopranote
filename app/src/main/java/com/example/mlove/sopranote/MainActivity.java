package com.example.mlove.sopranote;


import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Handler;
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

        noteImageViews = new ImageView[]{A, A, B, C, C, D, D, E, F, F, G, G};
        secondNoteImages = new ImageView[]{A2, A2, B2, C2, C2, D2, D2, E2, F2, F2, G2, G2};
        thirdNoteImages = new ImageView[]{A3, A3, B3, C3, C3, D3, D3, E3, F3, F3, G3, G3};
        fourthNoteImages = new ImageView[]{A4, A4, B4, C4, C4, D4, D4, E4, F4, F4, G4, G4};
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
            } else if (shouldWrite) {
                listOfAllStringNotes.add("Rest");
            }
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
                if (i != 0 && !(tempNote.noteEquals(listOfAllStringNotes.get(i - 1)))
                        && !(tempNote.noteEquals(listOfAllStringNotes.get(i + 1)))) {
                    tempNote = new Note(listOfAllStringNotes.get(i + 1), 1);
                }
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
                    if (firstTempImage != noteImageViews[i]) {
                        if (firstTempImage != null) {
                            firstTempImage.setVisibility(View.INVISIBLE);
                        }
                        if (firstNote.getQuaverType().equals("Eighth")) {
                            noteImageViews[i].setImageResource(R.drawable.eighth_note);
                        } else {
                            noteImageViews[i].setImageResource(R.drawable.backwards_note);
                        }
                        noteImageViews[i].setVisibility(View.VISIBLE);
                        firstTempImage = noteImageViews[i];
                    }
                }
                if (secondNote.getNote().equals(trueStringNoteValues[i])) {
                    if (secondTempImage != secondNoteImages[i]) {
                        if (secondTempImage != null) {
                            secondTempImage.setVisibility(View.INVISIBLE);
                        }
                        if (secondNote.getQuaverType().equals("Eighth")) {
                            secondNoteImages[i].setImageResource(R.drawable.eighth_note);
                        } else {
                            secondNoteImages[i].setImageResource(R.drawable.backwards_note);
                        }
                        secondNoteImages[i].setVisibility(View.VISIBLE);
                        secondTempImage = noteImageViews[i];
                    }
                }
                if (thirdNote.getNote().equals(trueStringNoteValues[i])) {
                    if (thirdTempImage != thirdNoteImages[i]) {
                        if (thirdTempImage != null) {
                            thirdTempImage.setVisibility(View.INVISIBLE);
                        }
                        if (thirdNote.getQuaverType().equals("Eighth")) {
                            thirdNoteImages[i].setImageResource(R.drawable.eighth_note);
                        } else {
                            thirdNoteImages[i].setImageResource(R.drawable.backwards_note);
                        }
                        thirdNoteImages[i].setVisibility(View.VISIBLE);
                        thirdTempImage = thirdNoteImages[i];
                        }
                    }
                if (fourthNote.getNote().equals(trueStringNoteValues[i])) {
                    if (fourthTempImage != fourthNoteImages[i]) {
                        if (fourthTempImage != null) {
                            fourthTempImage.setVisibility(View.INVISIBLE);
                        }
                        if (fourthNote.getQuaverType().equals("Eighth")) {
                            fourthNoteImages[i].setImageResource(R.drawable.eighth_note);
                        } else {
                            fourthNoteImages[i].setImageResource(R.drawable.backwards_note);
                        }
                        fourthNoteImages[i].setVisibility(View.VISIBLE);
                        fourthTempImage = fourthNoteImages[i];
                    }
                }
            }
            Log.d("print first note", "firstNote: " + firstNote.getNote());
            Log.d("print duration", "firstNote: " + firstNote.getDuration());
            Log.d("print second note", "secondNote: " + secondNote.getNote());
            Log.d("print duration", "secondNote: " + secondNote.getDuration());
            Log.d("print third note", "thirdNote: " + thirdNote.getNote());
            Log.d("print duration", "thirdNote: " + thirdNote.getDuration());
            Log.d("print fourth note", "fourthNote: " + fourthNote.getNote());
            Log.d("print duration", "fourthNote: " + fourthNote.getDuration());
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

}

