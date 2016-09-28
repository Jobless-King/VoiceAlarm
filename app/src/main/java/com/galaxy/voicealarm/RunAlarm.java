package com.galaxy.voicealarm;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RunAlarm extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_alarm);

        mediaPlayer = new MediaPlayer()
    }


}
