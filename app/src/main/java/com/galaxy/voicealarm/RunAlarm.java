package com.galaxy.voicealarm;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.naver.naverspeech.kfgd_naver.IManagerCommand;
import com.naver.naverspeech.kfgd_naver.NaverSpeechManager;

public class RunAlarm extends AppCompatActivity implements IManagerCommand {
    NaverSpeechManager naverSpeechManager;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private String[] voice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_alarm);

        //naverSpeechManager = NaverSpeechManager.CreateNaverSpeechManager(this, "54px6Qsc2zprZKsMMc4p");

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.escape);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        long[] pattern = { 0, 500, 200, 400, 100 };

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, 2);
    }
    @Override
    public void clientReady() {}
    @Override
    public void audioRecording(short[] text) {}
    @Override
    public void partialResult(String partialText) {}
    @Override
    public void finalResult(String[] finalText) {
        voice = finalText;
    }
    @Override
    public void recognitionError(String errorText) {}
    @Override
    public void clientInactive() {
        
    }
    public void MicOn(View view){
        if (!naverSpeechManager.getRecognizeState()) {
            // Start button is pushed when SpeechRecognizer's state is inactive.
            // Run SpeechRecongizer by calling recognize().

            naverSpeechManager.startRecognize();
        } else {
            // This flow is occurred by pushing start button again
            // when SpeechRecognizer is running.
            // Because it means that a user wants to cancel speech
            // recognition commonly, so call stop().

            naverSpeechManager.stopRecognize();
        }

    }
    public void Kill(){
        mediaPlayer.stop();
        vibrator.cancel();
    }
    @Override
    public void onBackPressed(){
        Toast.makeText(this, "안되 아니야 못꺼 자지마 일어나", Toast.LENGTH_SHORT).show();
    }


}
