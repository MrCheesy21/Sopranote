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

    private Button recordBtn, stopBtn, playBtn;
    private MediaRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordBtn = (Button) findViewById(R.id.record);
        stopBtn = (Button) findViewById(R.id.stop);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        recorder = new MediaRecorder();
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                final float pitchInHz = result.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView text = (TextView) findViewById(R.id.pitch);
                        text.setText("" + pitchInHz);
                    }
                });
            }
        };
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(p);
        new Thread(dispatcher,"Audio Dispatcher").start();
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setOutputFile("/dev/null");
                try {
                    recorder.prepare();
                    recorder.start();
                } catch (IllegalStateException | IOException e) {
                    Toast.makeText(getApplicationContext(), "error, please try again", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                recordBtn.setEnabled(false);
                stopBtn.setEnabled(true);
                Toast.makeText(getApplicationContext(), "recording started!", Toast.LENGTH_LONG).show();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stop();
                recorder.reset();
                //recorder.release();
                //recorder = null;
                stopBtn.setEnabled(false);
                recordBtn.setEnabled(true);
                playBtn.setEnabled(true);
                Toast.makeText(getApplicationContext(), "recording stopped, maxAmp = ", Toast.LENGTH_LONG).show();
            }
        });
    }
}



