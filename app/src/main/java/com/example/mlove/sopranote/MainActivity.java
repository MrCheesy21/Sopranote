package com.example.mlove.sopranote;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class MainActivity extends AppCompatActivity {

    TextView note, pitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pitch = findViewById(R.id.pitch);
        note = findViewById(R.id.note);
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

        pitch.setText("" + pitchInHz);

        if(pitchInHz >= 110 && pitchInHz < 123.47) {
            //A
            note.setText("A");
        }
        else if(pitchInHz >= 123.47 && pitchInHz < 130.81) {
            //B
            note.setText("B");
        }
        else if(pitchInHz >= 130.81 && pitchInHz < 146.83) {
            //C
            note.setText("C");
        }
        else if(pitchInHz >= 146.83 && pitchInHz < 164.81) {
            //D
            note.setText("D");
        }
        else if(pitchInHz >= 164.81 && pitchInHz <= 174.61) {
            //E
            note.setText("E");
        }
        else if(pitchInHz >= 174.61 && pitchInHz < 185) {
            //F
            note.setText("F");
        }
        else if(pitchInHz >= 185 && pitchInHz < 196) {
            //G
            note.setText("G");
        }

    }
}



