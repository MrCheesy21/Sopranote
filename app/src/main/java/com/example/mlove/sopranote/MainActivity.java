package com.example.mlove.sopranote;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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

public class MainActivity extends AppCompatActivity implements TempoInputDialog.TempoInputListener {

    private TextView note, pitch;
    private ImageView A, B, C,  D, E, F, G;
    private final String[] noteVals = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    private int[] noteIDs;
    private ImageView[] noteImages;
    private ImageView tempImage;
    private Button tempoInputButton;
    private Button tempoStopButton;
    private MediaPlayer tempoPlayer;
    private TextView TempoView;
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
        noteIDs = new int[]{R.id.A, R.id.A, R.id.B, R.id.C, R.id.C, R.id.D, R.id.D, R.id.E, R.id.F,
            R.id.F, R.id.G, R.id.G};
        pitch = findViewById(R.id.txtFrequency);
        note = findViewById(R.id.txtNote);
        tempoInputButton = findViewById(R.id.TempoInputButton);
        TempoView = findViewById(R.id.tempo);
        tempoStopButton = findViewById(R.id.tempoStopButton);
        tempoPlayer = MediaPlayer.create(this, R.raw.metronome_beep);

        tempoInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTempoDialog();
            }
        });

        tempoStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempoPlayer.pause();
        }
        });

        noteImages = new ImageView[]{A, A, B, C, C, D, D, E, F, F, G, G};
        tempImage = noteImages[0];
        for (ImageView e: noteImages) {
            e.setVisibility(View.GONE);
        }
        shiftPitches(noteVals);

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

    public void openTempoDialog() {
        TempoInputDialog  tempoInputDialog = new TempoInputDialog();
        tempoInputDialog.show(getSupportFragmentManager(), "tempo input dialog");
    }


    public void processPitch(float pitchInHz) {
        if (pitchInHz != -1.0) {
            pitch.setText(String.format("%.2f", pitchInHz));
            note.setText(notes[(int) pitchInHz]);
            for (int i = 0; i < noteImages.length; i++) {
                if (findViewById(noteIDs[i]) == noteImages[i] && note.getText().equals(noteVals[i])){
                    if (tempImage != noteImages[i]) {
                        tempImage.setVisibility(View.INVISIBLE);
                        noteImages[i].setVisibility(View.VISIBLE);
                        tempImage = noteImages[i];
                    }
                }
            }

        }
    }

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

    @Override
    public void setTempo(final String tempo) {
        if (tempo != "") {
            TempoView.setText(tempo + " BPM");
            final Handler tempoHandler = new Handler();
            final Runnable tempoRunner = new Runnable() {
                @Override
                public void run() {
                    tempoPlayer.start();
                    tempoHandler.postDelayed(this, 6500/Integer.parseInt(tempo));
                }
            };
            tempoRunner.run();
        }

    }
}

