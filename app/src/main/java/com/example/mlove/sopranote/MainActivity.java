package com.example.mlove.sopranote;

import android.media.Image;
import android.opengl.Visibility;
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
    ImageView A, Asharp, B, C, Csharp,  D, Dsharp, E, F, Fsharp, G, Gsharp;
    private static final double BASE = 440.0;
    private final String[] notes = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    private ImageView[] noteImages;
    private ImageView tempImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        A = findViewById(R.id.A);
        Asharp = findViewById(R.id.Asharp);
        B = findViewById(R.id.B);
        C = findViewById(R.id.C);
        Csharp = findViewById(R.id.Csharp);
        D = findViewById(R.id.D);
        Dsharp = findViewById(R.id.Dsharp);
        E = findViewById(R.id.E);
        F = findViewById(R.id.F);
        Fsharp = findViewById(R.id.Fsharp);
        G = findViewById(R.id.G);
        Gsharp = findViewById(R.id.Gsharp);
        noteImages = new ImageView[]{A, Asharp, B, C, Csharp, D, Dsharp, E, F, Fsharp, G, Gsharp};
        tempImage = noteImages[0];
        for (ImageView e: noteImages) {
            e.setVisibility(View.GONE);
        }
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
            pitch.setText("" + pitchInHz);
            float temp = Math.abs(pitchInHz);
            int index = (int) Math.abs(determineFreq(temp));
            int tempIndex = index % 12;
            if (pitchInHz < BASE) {
                tempIndex = 11 - tempIndex;
            }
                note.setText(notes[tempIndex]);
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
                if (findViewById(R.id.Asharp) == e &&  note.getText().equals("A#")) {
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
                if (findViewById(R.id.Csharp) == e &&  note.getText().equals("C#")) {
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
                if (findViewById(R.id.Dsharp) == e &&  note.getText().equals("D#")) {
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
                if (findViewById(R.id.Gsharp) == e &&  note.getText().equals("G#")) {
                    if (tempImage != e) {
                        tempImage.setVisibility(View.INVISIBLE);
                        e.setVisibility(View.VISIBLE);
                        tempImage = e;
                    }
                }
            }
        }
    }

    static int determineFreq(float pitch) {
        return (int) (12 * log(pitch/BASE, 2));
    }

    static double log(double x, double base) {
        return Math.log(x)/Math.log(base);
    }
}

