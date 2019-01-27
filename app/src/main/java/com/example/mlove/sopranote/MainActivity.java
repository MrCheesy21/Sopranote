package com.example.mlove.sopranote;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
    private Button TempoInputButton;
    private final String[] noteVals = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    private ImageView[] noteImages;
    private ImageView tempImage;
    private static final String[] notes = new String[5001];
    private static double range = 1.225;
    private static int cursor = 7;
    private static double index = 41;
    private static double ind = 41;


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
        shiftPitches(noteVals);
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



        TempoInputButton = findViewById(R.id.TempoInput);
        TempoInputButton.setOnClickListener(new View.OnClickListener() {
            final int tempo = 0;
            @Override
            public void onClick(View v) {
                DialogFragment TempoInput = new TempoInputDialogFragment();
            }
        });
        }

    public void processPitch(float pitchInHz) {
        if (pitchInHz != -1.0) {
            pitch.setText(String.format("%.2f", pitchInHz));
            note.setText(notes[(int) pitchInHz]);
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
                if (findViewById(R.id.A) == e &&  note.getText().equals("A#")) {
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
                if (findViewById(R.id.C) == e &&  note.getText().equals("C#")) {
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
                if (findViewById(R.id.D) == e &&  note.getText().equals("D#")) {
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
                if (findViewById(R.id.F) == e &&  note.getText().equals("F#")) {
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
                if (findViewById(R.id.G) == e &&  note.getText().equals("G#")) {

                    if (tempImage != e) {
                        tempImage.setVisibility(View.INVISIBLE);
                        e.setVisibility(View.VISIBLE);
                        tempImage = e;
                    }
                }
            }
        }
    }

    /*
    This algorithm uses the equal tempered tuning system to determine when a note corresponds to
    a certain frequency. Each note's frequencies are exactly the frequency of the note before it
    times the 12th root of 2. Since there are 12 possible notes in an octave, A4 will be exactly
    double the frequency of A3.
     */
    public void shiftPitches(String[] noteVals) {
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


