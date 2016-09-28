package com.galaxy.voicealarm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private String selectedSchedule = "지정어";
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

        blink.setVisibility(View.VISIBLE);
        selectedType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.Auto:
                        blink.setVisibility(View.GONE);
                        break;
                    case R.id.Hand:
                        blink.setVisibility(View.VISIBLE);
                        break;
                    case R.id.None:
                        blink.setVisibility(View.GONE);
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
        int time = selectedHour*100+selectedMinute;
        String speaking = "";

        //DBHelper dbHelper = new DBHelper(getApplicationContext(), "Alarm");   //수정: KFGD
        DBHelper dbHelper = DBHelper.getInstance();
        if(time == 404)
            Toast.makeText(this, "충격과 공포다 그지 깡깡이들아", Toast.LENGTH_SHORT).show();
        dbHelper.query("INSERT INTO Alarm VALUES(null, " + 1111111 + ", " + time + ", '" + speaking + "')");
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

