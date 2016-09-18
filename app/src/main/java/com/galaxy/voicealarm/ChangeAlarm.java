package com.galaxy.voicealarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class ChangeAlarm extends AppCompatActivity {

    private ArrayList<String> arraylist;
    private Button outputTime;
    private Spinner automaticInput;
    private int selectedHour, selectedMinute;
    private String selectedSchedule = "지정어";
    static final int TIME_DIALOG_ID=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_alarm);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id",0);


    }

    public void Change(View view) {
        Intent intent=new Intent(ChangeAlarm.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void Cancel(View view) {
        Intent intent=new Intent(ChangeAlarm.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void Delete(View view) {
        Intent intent=new Intent(ChangeAlarm.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
