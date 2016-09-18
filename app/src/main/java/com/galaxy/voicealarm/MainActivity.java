package com.galaxy.voicealarm;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.naver.naverspeech.kfgd_naver.IManagerCommand;
import com.naver.naverspeech.kfgd_naver.NaverSpeechManager;

public class MainActivity extends AppCompatActivity {
    NaverSpeechManager naverSpeechManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        naverSpeechManager = NaverSpeechManager.CreateNaverSpeechManager(this, "", new VoiceManager());
    }



    public void RunAddAlarm(View view){
        Intent intent=new Intent(MainActivity.this, AddAlarm.class);
        startActivity(intent);
        finish();
    }
}
