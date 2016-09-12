package com.galaxy.voicealarm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AddAlarm extends AppCompatActivity {

    ArrayList<String> arraylist;
    private TextView outputDate,  outputTime;
    Spinner automaticInput;
    private int selectedYear, selectedMonth, selectedDay;
    private int selectedHour, selectedMinute;
    private String selectedSchedule = "지정어";
    static final int DATE_DIALOG_ID=0;
    static final int TIME_DIALOG_ID=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        outputTime = (TextView)findViewById(R.id.OutputTime);
        outputDate = (TextView)findViewById(R.id.OutputDate);
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
    public void InputDate(View view){
        showDialog(DATE_DIALOG_ID);
    }
    public void InputTime(View view){
        showDialog(TIME_DIALOG_ID);
    }
    private DatePickerDialog.OnDateSetListener dpDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    selectedYear = year%100;
                    selectedMonth = monthOfYear+1;
                    selectedDay =  dayOfMonth;
                    outputDate.setText(year + "/" + selectedMonth + "/" + selectedDay);
                }
            };
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
        switch(id){
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, dpDateSetListener, 2016, 9, selectedDay);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, tpTimeSetListenet, selectedHour, selectedMinute, true);
            default:
                return null;
        }
    }

    public void Add(View view){
        Intent intent=new Intent(AddAlarm.this, RunAlarm.class);
        startActivity(intent);
        finish();
    }

    public void Cancel(View view) {
        Intent intent=new Intent(AddAlarm.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

