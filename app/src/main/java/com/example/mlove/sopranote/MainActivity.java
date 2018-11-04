package com.example.mlove.sopranote;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    ImageView A, B, C, D, E, F, G, E2, F2;
    private static final double BASE = 440.0;
    private final String[] notes = {"A", "A", "B", "C", "C", "D", "D", "E", "F", "F", "G", "G"};


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
        E2 = findViewById(R.id.E2);
        F2 = findViewById(R.id.F2);
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
<<<<<<< HEAD
        pitch.setText("" + pitchInHz);
        float temp = Math.abs(pitchInHz);
        int index = determineFreq(temp);
        note.setText(notes[Math.abs(index)%12]);

||||||| merged common ancestors
        pitch.setText("" + pitchInHz);
        float temp = Math.abs(pitchInHz);
        int index = (int) determineFreq(temp);
        if (index > 0 && index < 7) {
            note.setText(notes[index%7]);
        }
=======
        if (pitchInHz != -1.0) {
            pitch.setText("" + pitchInHz);
            float temp = Math.abs(pitchInHz);
            int index = (int) Math.abs(determineFreq(temp));
            note.setText(notes[index%12]);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
>>>>>>> 743fe4668577f1c97308244ffe904c28a3771b64
    }

    static int determineFreq(float pitch) {
        return (int) (12 * log(pitch/BASE, 2));
    }

    static double log(double x, double base) {
        return Math.log(x)/Math.log(base);
    }
}

