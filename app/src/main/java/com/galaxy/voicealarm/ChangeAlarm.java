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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class ChangeAlarm extends AppCompatActivity {
    private int position;
    private int _id;
    private Button outputTime;
    private ToggleButton mon, tue, wed, thu, fri, sat, sun;
    private RadioGroup selectedType;
    private LinearLayout blink;
    private EditText speaked;
    private int selectedHour, selectedMinute;
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

        outputTime = (Button)findViewById(R.id.OutputTimeC);
        mon = (ToggleButton) findViewById(R.id.MonC);
        tue = (ToggleButton) findViewById(R.id.TueC);
        wed = (ToggleButton) findViewById(R.id.WedC);
        thu = (ToggleButton) findViewById(R.id.ThuC);
        fri = (ToggleButton) findViewById(R.id.FriC);
        sat = (ToggleButton) findViewById(R.id.SatC);
        sun = (ToggleButton) findViewById(R.id.SunC);
        selectedType = (RadioGroup)findViewById(R.id.SelectTypeC);
        blink = (LinearLayout)findViewById(R.id.BlinkC);
        speaked = (EditText) findViewById(R.id.SpeakedC);

        ToogelOnClick();

        dbHelper = DBHelper.getInstance();
        sql = dbHelper.getWritableDatabase();
        cursor = sql.rawQuery("SELECT * FROM Alarm;", null);
        if(cursor.getCount() > 0){
            startManagingCursor(cursor);
            cursor.moveToPosition(position);
            _id = cursor.getInt(cursor.getColumnIndex("_id"));
            speaked.setText(cursor.getString(cursor.getColumnIndex("speaking")));
        }
        blink.setVisibility(View.GONE);
        selectedType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.AutoC:
                        blink.setVisibility(View.GONE);
                        speaked.setText("");
                        break;
                    case R.id.HandC:
                        blink.setVisibility(View.VISIBLE);
                        break;
                    case R.id.NoneC:
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

    public void Change(View view) {
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
        dbHelper.query("UPDATE Alarm set week="+week+", time="+time+", speaking='"+speaking+"' where _id = "+_id);
        Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
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
        dbHelper.query("DELETE FROM Alarm WHERE _id='" + _id + "';");
        Toast.makeText(this, " 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(ChangeAlarm.this, AlarmList.class);
        startActivity(intent);
        finish();
    }
    private void ToogelOnClick(){
        mon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mon.isChecked())
                    mon.setBackgroundDrawable(getResources().getDrawable(R.drawable.monon));
                else
                    mon.setBackgroundDrawable(getResources().getDrawable(R.drawable.monoff));
            }
        });
        tue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(tue.isChecked())
                    tue.setBackgroundDrawable(getResources().getDrawable(R.drawable.tueon));
                else
                    tue.setBackgroundDrawable(getResources().getDrawable(R.drawable.tueoff));
            }
        });
        wed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(wed.isChecked())
                    wed.setBackgroundDrawable(getResources().getDrawable(R.drawable.wedon));
                else
                    wed.setBackgroundDrawable(getResources().getDrawable(R.drawable.wedoff));
            }
        });
        thu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(thu.isChecked())
                    thu.setBackgroundDrawable(getResources().getDrawable(R.drawable.thuon));
                else
                    thu.setBackgroundDrawable(getResources().getDrawable(R.drawable.thuoff));
            }
        });
        fri.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(fri.isChecked())
                    fri.setBackgroundDrawable(getResources().getDrawable(R.drawable.frion));
                else
                    fri.setBackgroundDrawable(getResources().getDrawable(R.drawable.frioff));
            }
        });
        sat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(sat.isChecked())
                    sat.setBackgroundDrawable(getResources().getDrawable(R.drawable.saton));
                else
                    sat.setBackgroundDrawable(getResources().getDrawable(R.drawable.satoff));
            }
        });
        sun.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(sun.isChecked())
                    sun.setBackgroundDrawable(getResources().getDrawable(R.drawable.sunon));
                else
                    sun.setBackgroundDrawable(getResources().getDrawable(R.drawable.sunoff));
            }
        });
    }
}
