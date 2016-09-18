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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class AddAlarm extends AppCompatActivity {

    private ArrayList<String> arraylist;
    private Button outputTime;
    private Spinner automaticInput;
    private int selectedHour, selectedMinute;
    private String selectedSchedule = "지정어";
    static final int TIME_DIALOG_ID=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        outputTime = (Button)findViewById(R.id.OutputTime);
        arraylist = new ArrayList<String>();
        arraylist.add("어서 "+selectedSchedule+" 해라");
        arraylist.add("두번 "+selectedSchedule+" 해라");
        arraylist.add("그만자고 "+selectedSchedule+" 해라");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraylist);
        automaticInput = (Spinner)findViewById(R.id.AutomaticInput);
        automaticInput.setPrompt("문장 선택");
        automaticInput.setAdapter(adapter);
        automaticInput.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Object item = arg0.getItemAtPosition(arg2);
                if (item!=null) {
                    Toast.makeText(AddAlarm.this, item.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
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
        String speaking = (String)automaticInput.getSelectedItem();

        DBHelper dbHelper = new DBHelper(getApplicationContext(), "Alarm");
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

