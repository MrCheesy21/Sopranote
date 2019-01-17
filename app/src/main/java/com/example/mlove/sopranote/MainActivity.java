package com.example.mlove.sopranote;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class MainActivity extends AppCompatActivity {

    TextView note, pitch;
    ImageView A, B, C,  D, E, F, G;
    private static final double BASE = 440.0;
    private final String[] notes = {"A", "A", "B", "C", "C", "D", "D", "E", "F", "F", "G", "G"};
    private ImageView[] noteImages;
    private ImageView tempImage;
    private static final String[] allNotes = new String[5001];
    private static final String[] newNotes = new String[5001];
    private static double previous = 41;
    private static double current = 41;
    private static int counter = -40;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        A = findViewById(R.id.A);
        B = findViewById(R.id.B);
        C = findViewById(R.id.C);
        D = findViewById(R.id.D);
        E = findViewById(R.id.E);
        F = findViewById(R.id.F);
        G = findViewById(R.id.G);
        noteImages = new ImageView[]{A, A, B, C, C, D, D, E, F, F, G, G};
        tempImage = noteImages[0];
        for (ImageView e: noteImages) {
            e.setVisibility(View.GONE);
        }
        shiftPitches();
        pitch = findViewById(R.id.txtFrequency);
        note = findViewById(R.id.txtNote);
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
        }

    public void processPitch(float pitchInHz) {
        if (pitchInHz != -1.0) {
            pitch.setText(String.format("%.2f", pitchInHz));
            note.setText(newNotes[(int) pitchInHz]);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (ImageView e: noteImages) {
                if (findViewById(R.id.A) == e &&  note.getText().equals("A")) {
                    if (tempImage != e) {
                        tempImage.setVisibility(View.INVISIBLE);
                        e.setVisibility(View.VISIBLE);
                        tempImage = e;
                    }
                }
                if (findViewById(R.id.B) == e &&  note.getText().equals("B")) {
                    if (tempImage != e) {
                        tempImage.setVisibility(View.INVISIBLE);
                        e.setVisibility(View.VISIBLE);
                        tempImage = e;
                    }
                }
                if (findViewById(R.id.C) == e &&  note.getText().equals("C")) {
                    if (tempImage != e) {
                        tempImage.setVisibility(View.INVISIBLE);
                        e.setVisibility(View.VISIBLE);
                        tempImage = e;
                    }
                }
                if (findViewById(R.id.D) == e &&  note.getText().equals("D")) {
                    if (tempImage != e) {
                        tempImage.setVisibility(View.INVISIBLE);
                        e.setVisibility(View.VISIBLE);
                        tempImage = e;
                    }
                }
                if (findViewById(R.id.E) == e &&  note.getText().equals("E")) {
                    if (tempImage != e) {
                        tempImage.setVisibility(View.INVISIBLE);
                        e.setVisibility(View.VISIBLE);
                        tempImage = e;
                    }
                }
                if (findViewById(R.id.F) == e &&  note.getText().equals("F")) {
                    if (tempImage != e) {
                        tempImage.setVisibility(View.INVISIBLE);
                        e.setVisibility(View.VISIBLE);
                        tempImage = e;
                    }
                }
                if (findViewById(R.id.G) == e &&  note.getText().equals("G")) {
                    if (tempImage != e) {
                        tempImage.setVisibility(View.INVISIBLE);
                        e.setVisibility(View.VISIBLE);
                        tempImage = e;
                    }
                }
            }
        }
    }

    public void shiftPitches() {
        for (int i = 40; i <= 5000; i++) {
            float temp = Math.abs(i);
            int index = Math.abs(determineFreq(temp));
            int tempIndex = index % 12;
            if (i < BASE) {
                tempIndex = 11 - tempIndex;
            }
            String tempNote = notes[tempIndex];
            allNotes[i] = tempNote;
        }
        for (int i = 40; i <= 5000; i++) {
            boolean special = false;
            if (i < (int)  (getCurrent())) {
                newNotes[i] = allNotes[i];
            } else if (i == (int)  (getCurrent())) {
                getNextFreq(counter++);
                int temp = (int)  (getCurrent());
                if (temp <= 1000) {
                    if (allNotes[(int)((getCurrent() - getPrevious()) / 2.0 + getPrevious())].equals("B")
                            || allNotes[(int)((getCurrent() - getPrevious()) / 2.0 + getPrevious())].equals("E")) {
                        temp = (int) ((getCurrent() - getPrevious()) / 2.0 + getPrevious());
                        special = true;
                    }
                }
                int holder = temp;
                while (i < holder) {
                    if (temp > 1000) {
                        temp = 999;
                        holder = temp;
                    }
                    if (special) {
                        while (allNotes[temp].equals("B") || allNotes[temp].equals("E")) {
                            temp++;
                        }
                        special = false;
                    }
                    newNotes[i] = allNotes[holder + 1];
                    i++;
                }
                while (i < temp - 1) {
                    newNotes[i] = allNotes[temp];
                    i++;
                }
                i--;
            }
        }
    }

    private static int determineFreq(float pitch) {
        return (int) (12 * log(pitch/BASE, 2));
    }

    private static double log(double x, double base) {
        return Math.log(x)/Math.log(base);
    }

    private static void getNextFreq(int index) {
        previous = current;
        current = 440.0 * Math.pow(2, ((index + 1.0) / 12.0));
    }

    private static double getCurrent() {
        return current;
    }

    private static double getPrevious() {
        return previous;
    }
}

