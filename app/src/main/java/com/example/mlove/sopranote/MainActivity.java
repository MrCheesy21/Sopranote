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
    private TextView note, pitch;
    private ImageView A, B, C, D, E, F, G, A2, B2, C2, D2, E2, F2, G2, A3, B3, C3, D3, E3, F3, G3, A4, B4, C4, D4, E4, F4, G4;
    private final String[] noteVals = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    private int[] noteIDs;
    private ImageView[] noteImages;
    private ImageView[] secondNoteImages;
    private ImageView[] thirdNoteImages;
    private ImageView[] fourthNoteImages;


    private ImageView tempImage;

    private Button tempoInputButton;
    private Button tempoStopButton;
    private MediaPlayer tempoPlayer;
    private Handler tempoHandler;
    private TextView TempoView;
    private Runnable tempoRunner;
    private int interval;


    private ArrayList<String> melodyList;
    private Button writeArray;
    private Button stopWriting;
    private boolean shouldWrite, shouldDisplay;
    private TextView noteDisplay;

    private static final String[] notes = new String[10001];
    private static double range = 1.225;
    private static int cursor = 7;
    private static double index = 41;
    private static double ind = 41;


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
        noteIDs = new int[]{R.id.a1, R.id.a1, R.id.b1, R.id.c1, R.id.c1, R.id.d1, R.id.d1, R.id.e1, R.id.f1,
            R.id.f1, R.id.g1, R.id.g1};
        pitch = findViewById(R.id.txtFrequency);
        note = findViewById(R.id.txtNote);
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
        noteImages = new ImageView[]{A, A, B, C, C, D, D, E, F, F, G, G};
        secondNoteImages = new ImageView[]{A2, A2, B2, C2, C2, D2, D2, E2, F2, F2, G2, G2};
        thirdNoteImages = new ImageView[]{A3, A3, B3, C3, C3, D3, D3, E3, F3, F3, G3, G3};
        fourthNoteImages = new ImageView[]{A4, A4, B4, C4, C4, D4, D4, E4, F4, F4, G4, G4};

        tempImage = noteImages[0];
        clearImages();
        shiftPitches(noteVals);

        melodyList = new ArrayList<>(10000);
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
                shouldDisplay = false;
                filterMelodyList();
            }
        });

        writeArray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                melodyList.clear();
                shouldWrite = true;
                shouldDisplay = true;
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
            pitch.setText(String.format("%.2f", pitchInHz));
            note.setText(notes[(int) pitchInHz]);

            if (shouldWrite) {
                melodyList.add(notes[(int) pitchInHz]);
            }
//            if (shouldDisplay) {
//                for (int i = 0; i < noteImages.length; i++) {
//                    if (note.getText().equals(noteVals[i])){
//                        if (tempImage != noteImages[i]) {
//                            tempImage.setVisibility(View.INVISIBLE);
//                            noteImages[i].setVisibility(View.VISIBLE);
//                            tempImage = noteImages[i];
//                        }
//                    }
//                }
//            }

        } else if (shouldWrite) {
            melodyList.add("Rest");
        }
    }

    private void filterMelodyList() {
        final String TAG = "filtering the notes";
        noteDisplay.setText("");
        Note tempNote = new Note("", 0);

        int startIndex = 0;
        while(startIndex < melodyList.size() && melodyList.get(startIndex).equals("Rest")) {
            startIndex++;
        }
        ArrayList<Note> filteredList = new ArrayList<>();
        for (int i = startIndex; i < melodyList.size() - 1; i++) {
            Log.i(TAG, "Note at time " + i * 50 + ": " + melodyList.get(i));
            if (!tempNote.equals(melodyList.get(i))) {
                tempNote = new Note(melodyList.get(i), 0);
                if (i != 0 && !(tempNote.noteEquals(melodyList.get(i - 1)))
                        && !(tempNote.noteEquals(melodyList.get(i + 1)))) {
                    tempNote = new Note(melodyList.get(i + 1), 1);
                }
                while(i + tempNote.getDuration() < melodyList.size()
                        && tempNote.noteEquals(melodyList.get(i + tempNote.getDuration()))) {
                    tempNote.incrementDurationBy(1);
                }
                if (tempNote.getDuration() > 2) {
                    filteredList.add(tempNote);
                }
                i += tempNote.getDuration();
            }
        }
        for (int i = 0; i < filteredList.size() - 1; i++) {
            if (filteredList.get(i).noteEquals(filteredList.get(i + 1).getNote())) {
                filteredList.get(i + 1).incrementDurationBy(filteredList.get(i).getDuration());
                filteredList.remove(i);
            }
            noteDisplay.append(filteredList.get(i).toString());
        }
        displayAllNotes(filteredList);
    }

    public void displayAllNotes(List<Note> noteDisplayList) {
        clearImages();
        if (!noteDisplayList.isEmpty()) {
            boolean wroteFirst = false, wroteSecond = false, wroteThird = false, wroteFourth = false;
            ImageView firstTempImage = noteImages[0];
            ImageView secondTempImage = secondNoteImages[0];
            ImageView thirdTempImage = thirdNoteImages[0];
            ImageView fourthTempImage = fourthNoteImages[0];
            Note firstNote = new Note("Rest", 0),
                    secondNote = new Note("Rest", 0),
                    thirdNote = new Note("Rest", 0),
                    fourthNote = new Note("Rest", 0);
            int noteDisplayListIndex = 0;
            if (noteDisplayList.get(0).noteEquals("Rest")) {
                noteDisplayListIndex++;
            }

            while (noteDisplayListIndex < noteDisplayList.size() && (!wroteFirst || !wroteSecond || !wroteThird)) {
                if (!wroteFirst && !noteDisplayList.get(noteDisplayListIndex).noteEquals("Rest")) {
                    wroteFirst = true;
                    firstNote = noteDisplayList.get(noteDisplayListIndex);
                } else if (!wroteSecond && !noteDisplayList.get(noteDisplayListIndex).noteEquals("Rest")) {
                    wroteSecond = true;
                    secondNote = noteDisplayList.get(noteDisplayListIndex);
                } else if (!wroteThird && !noteDisplayList.get(noteDisplayListIndex).noteEquals("Rest")) {
                    wroteThird = true;
                    thirdNote = noteDisplayList.get(noteDisplayListIndex);
                } else if (!wroteFourth && !noteDisplayList.get(noteDisplayListIndex).noteEquals("Rest")) {
                    wroteFourth = true;
                    fourthNote = noteDisplayList.get(noteDisplayListIndex);
                }
                noteDisplayListIndex++;
            }
            for (int i = 0; i < secondNoteImages.length; i++) {
                if (firstNote.getNote().equals(noteVals[i])) {
                    if (firstTempImage != noteImages[i]) {
                        firstTempImage.setVisibility(View.INVISIBLE);
                        noteImages[i].setVisibility(View.VISIBLE);
                        firstTempImage = noteImages[i];
                    }
                }
                if (secondNote.getNote().equals(noteVals[i])) {
                    if (secondTempImage != secondNoteImages[i]) {
                        secondTempImage.setVisibility(View.INVISIBLE);
                        secondNoteImages[i].setVisibility(View.VISIBLE);
                        secondTempImage = noteImages[i];
                    }
                }
                if (thirdNote.getNote().equals(noteVals[i])) {
                    if (thirdTempImage != thirdNoteImages[i]) {
                        thirdTempImage.setVisibility(View.INVISIBLE);
                        thirdNoteImages[i].setVisibility(View.VISIBLE);
                        thirdTempImage = thirdNoteImages[i];
                        }
                    }
                if (fourthNote.getNote().equals(noteVals[i])) {
                    if (fourthTempImage != fourthNoteImages[i]) {
                        fourthTempImage.setVisibility(View.INVISIBLE);
                        fourthNoteImages[i].setVisibility(View.VISIBLE);
                        fourthTempImage = thirdNoteImages[i];
                    }
                }
            }

        }
    }

    private void clearImages() {
        for (int i = 0; i < noteImages.length; i++) {
            noteImages[i].setVisibility(View.GONE);
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
    private void shiftPitches(String[] noteVals) {
        while (index < notes.length) {
            while (ind < index + range) {
                if (ind < notes.length) {
                    notes[(int) ind] =  noteVals[cursor % 12];
                }
                ind++;
            }
            cursor++;
            index += range * 2;
            nextRange();
        }
    }

    private static void nextRange() {
        range *= Math.cbrt(Math.sqrt(Math.sqrt(2)));
    }
}

