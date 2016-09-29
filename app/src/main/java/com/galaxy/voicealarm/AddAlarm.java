package com.galaxy.voicealarm;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AddAlarm extends AppCompatActivity {

    private Button outputTime;
    private ToggleButton mon, tue, wed, thu, fri, sat, sun;
    private RadioGroup selectedType;
    private LinearLayout blink;
    private EditText speaked;
    private int selectedHour, selectedMinute;
    static final int TIME_DIALOG_ID=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        outputTime = (Button)findViewById(R.id.OutputTime);
        mon = (ToggleButton) findViewById(R.id.Mon);
        tue = (ToggleButton) findViewById(R.id.Tue);
        wed = (ToggleButton) findViewById(R.id.Wed);
        thu = (ToggleButton) findViewById(R.id.Thu);
        fri = (ToggleButton) findViewById(R.id.Fri);
        sat = (ToggleButton) findViewById(R.id.Sat);
        sun = (ToggleButton) findViewById(R.id.Sun);
        selectedType = (RadioGroup)findViewById(R.id.SelectType);
        blink = (LinearLayout)findViewById(R.id.Blink);
        speaked = (EditText) findViewById(R.id.Speaked);

        blink.setVisibility(View.GONE);

        selectedType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.Auto:
                        blink.setVisibility(View.GONE);
                        speaked.setText("");
                        break;
                    case R.id.Hand:
                        blink.setVisibility(View.VISIBLE);
                        break;
                    case R.id.None:
                        blink.setVisibility(View.GONE);
                        speaked.setText("");
                        break;
                }
            }
        });
    }
    public void InputTime(View view){
        showDialog(TIME_DIALOG_ID);
    }
    private TimePickerDialog.OnTimeSetListener tpTimeSetListenet =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    selectedHour = hourOfDay;
                    selectedMinute = minute;
                    outputTime.setText(selectedHour+" : "+selectedMinute);
                }
            };
    @Override
    protected Dialog onCreateDialog(int id) {
        return new TimePickerDialog(this, tpTimeSetListenet, selectedHour, selectedMinute, true);
    }

    public void Add(View view){
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent Intent = new Intent(this, RunAlarm.class);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, Intent, 0);
        long settingTime = System.currentTimeMillis() - ((System.currentTimeMillis()+9*60*60*1000)%(24*60*60*1000)) + selectedHour*60*60*1000 + selectedMinute*60*1000;
//        alarmManager.set(AlarmManager.RTC, settingTime, pIntent);
        alarmManager.setRepeating(AlarmManager.RTC, settingTime, 24*60*60*1000, pIntent);
        Toast.makeText(this, String.valueOf((System.currentTimeMillis()+9*60*60*1000)%(24*60*60*1000)), Toast.LENGTH_SHORT).show();

        int time = selectedHour*100+selectedMinute;
        int week = 0;
        String speaking = speaked.getText().toString();
        if(speaking.equals(""))
            speaking = "일정 말하기";
        if(mon.isChecked())
            week = week+1;
        if(tue.isChecked())
            week = week+10;
        if(wed.isChecked())
            week = week+100;
        if(thu.isChecked())
            week = week+1000;
        if(fri.isChecked())
            week = week+10000;
        if(sat.isChecked())
            week = week+100000;
        if(sun.isChecked())
            week = week+1000000;
        DBHelper dbHelper = DBHelper.getInstance();
        dbHelper.query("INSERT INTO Alarm VALUES(null, " + week + ", " + time + ", '" + speaking + "', 1)");
        Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(AddAlarm.this, AlarmList.class);
        startActivity(intent);
        finish();
    }

    public void Cancel(View view) {
        Intent intent=new Intent(AddAlarm.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

