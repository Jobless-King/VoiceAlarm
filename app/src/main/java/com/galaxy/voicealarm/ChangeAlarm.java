package com.galaxy.voicealarm;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

public class ChangeAlarm extends AppCompatActivity {
    private int position;
    private int id;
    private ArrayList<String> arraylist;
    private Button outputTime;
    private EditText changeSpeaked;
    private Spinner automaticInput;
    private int selectedHour, selectedMinute;
    private String selectedSchedule = "지정어";
    static final int TIME_DIALOG_ID=1;
    private DBHelper dbHelper;
    private SQLiteDatabase sql;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_alarm);

        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);

        outputTime = (Button)findViewById(R.id.ChangeOutputTime);
        changeSpeaked = (EditText)findViewById(R.id.ChangeSpeaked);
        //dbHelper = new DBHelper(getApplicationContext(), "Alarm"); //수정: 김관용
        dbHelper = DBHelper.getInstance();
        sql = dbHelper.getWritableDatabase();
        cursor = sql.rawQuery("SELECT * FROM Alarm;", null);
        if(cursor.getCount() > 0){
            startManagingCursor(cursor);
            cursor.moveToPosition(position);
            id = cursor.getInt(cursor.getColumnIndex("_id"));
            changeSpeaked.setText(cursor.getString(cursor.getColumnIndex("speaking")));
        }
        arraylist = new ArrayList<String>();
        arraylist.add("어서 "+selectedSchedule+" 해라");
        arraylist.add("두번 "+selectedSchedule+" 해라");
        arraylist.add("그만자고 "+selectedSchedule+" 해라");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraylist);
        automaticInput = (Spinner)findViewById(R.id.ChangeAutomaticInput);
        automaticInput.setPrompt("문장 선택");
        automaticInput.setAdapter(adapter);
        automaticInput.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Object item = arg0.getItemAtPosition(arg2);
                if (item!=null) {
                    Toast.makeText(ChangeAlarm.this, item.toString(), Toast.LENGTH_SHORT).show();
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

    public void Change(View view) {
        Intent intent=new Intent(ChangeAlarm.this, AlarmList.class);
        startActivity(intent);
        finish();
    }
    public void Cancel(View view) {
        Intent intent=new Intent(ChangeAlarm.this, AlarmList.class);
        startActivity(intent);
        finish();
    }
    public void Delete(View view) {
        dbHelper.query("DELETE FROM Alarm WHERE _id='" + id + "';");
        Toast.makeText(this, id+" 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(ChangeAlarm.this, AlarmList.class);
        startActivity(intent);
        finish();
    }
}
