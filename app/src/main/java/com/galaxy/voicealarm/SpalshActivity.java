package com.galaxy.voicealarm;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SpalshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        if(null != getSupportActionBar())
            getSupportActionBar().hide();

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                startActivity(new Intent(SpalshActivity.this, MainActivity.class));
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0, 2000);
    }
}
