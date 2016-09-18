package com.galaxy.voicealarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AlarmList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
    }

    public void RunAddAlarm(View view){
        Intent intent=new Intent(AlarmList.this, AddAlarm.class);
        startActivity(intent);
        finish();
    }
}
