package com.galaxy.voicealarm;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void RunAddAlarm(View view){
        Intent intent=new Intent(MainActivity.this, AddAlarm.class);
        startActivity(intent);
        finish();
    }
}
