package com.example.mlove.sopranote;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button recordBtn, stopBtn, playBtn;
    private MediaRecorder recorder;
    private String outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordBtn = (Button) findViewById(R.id.record);
        stopBtn = (Button) findViewById(R.id.stop);
        playBtn = (Button) findViewById(R.id.play);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(outputFile);


        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    recorder.prepare();
                    recorder.start();
                } catch (IllegalStateException ise) {
                    //idk what to do here
                } catch (IOException ioe) {
                    //lmao idk exceptions
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
                recorder.release();
                recorder = null;
                stopBtn.setEnabled(false);
                recordBtn.setEnabled(true);
                playBtn.setEnabled(true);
                Toast.makeText(getApplicationContext(), "recording stopped", Toast.LENGTH_LONG).show();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(outputFile);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Toast.makeText(getApplicationContext(), "playing..", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        //idk
                    }
                }
            });

        }
    }



