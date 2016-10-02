package com.galaxy.voicealarm;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.galaxy.voicealarm.AddAlarm.ADD_ALARM_AUDIO;

public class ChangeAlarm extends AppCompatActivity {
    private int position;
    private int _id;
    private Button outputTime, outputMusic;
    private ToggleButton mon, tue, wed, thu, fri, sat, sun;
    private RadioGroup selectedType;
    private LinearLayout blink;
    private EditText speaked;
    private int pasttime, selectedHour, selectedMinute;
    static final int TIME_DIALOG_ID=1;
    private DBHelper dbHelper;
    private SQLiteDatabase sql;
    private Cursor cursor;

    private AudioFile selectedAudioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_alarm);

        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);

        outputTime = (Button)findViewById(R.id.OutputTimeC);
        outputMusic = (Button)findViewById(R.id.OutputMusic);
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
            pasttime = cursor.getInt(cursor.getColumnIndex("time"));

            String FilePath = cursor.getString(cursor.getColumnIndex("path"));
            int i=FilePath.length()-3;
            for(; i>0; --i){
                if('/' == FilePath.charAt(i))
                    break;
            }
            String FileName = FilePath.substring(i+1, FilePath.length()-3);
            selectedAudioFile = new AudioFile(FileName, FilePath);

            if(5 <= FileName.length())
               FileName =  FileName.substring(0, 5);
            ((Button)findViewById(R.id.OutputMusic)).setText(FileName);

            selectedHour = pasttime/100;
            selectedMinute = pasttime%100;
            outputTime.setText(selectedHour+ ": "+selectedMinute);
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

    public void InputMusicC(View view){
        Intent intent = new Intent(ChangeAlarm.this, SelectedAudioActivity.class);
        startActivityForResult(intent, ADD_ALARM_AUDIO);
    }

    public void PlayMusicC(View v){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(selectedAudioFile.getFilePath()), "audio/*");
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){
            case ADD_ALARM_AUDIO:{
                if(RESULT_OK == resultCode){
                    if(null == intent.getParcelableExtra("AUDIO_FILE"))
                        return;
                    selectedAudioFile = intent.getParcelableExtra("AUDIO_FILE");
                    if(5 <= selectedAudioFile.getFileName().length()){
                        ((Button) findViewById(R.id.OutputMusic)).setText(selectedAudioFile.getFileName().substring(0, 5));
                    }else {
                        ((Button) findViewById(R.id.OutputMusic)).setText(selectedAudioFile.getFileName());
                    }
                    Toast.makeText(ChangeAlarm.this, selectedAudioFile.getFileName() + "mp3파일이 선택되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
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
        dbHelper.query("UPDATE Alarm set week="+week+", time="+time+", speaking='"+speaking+"', path='"+ selectedAudioFile.getFilePath()+"' where _id = "+_id);

        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent Intent = new Intent(this, RunAlarm.class);

        sql = dbHelper.getWritableDatabase();
        cursor = sql.rawQuery("SELECT * FROM Alarm;", null);
        if(cursor.getCount() > 0) {
            startManagingCursor(cursor);
            cursor.moveToPosition(position);
        }
        PendingIntent sender = PendingIntent.getActivity(this, cursor.getInt(0), Intent, PendingIntent.FLAG_CANCEL_CURRENT);
        long settingTime = System.currentTimeMillis() - ((System.currentTimeMillis()+9*60*60*1000)%(24*60*60*1000)) + selectedHour*60*60*1000 + selectedMinute*60*1000;

        alarmManager.cancel(sender);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, settingTime, 24*60*60*1000,sender);

        DateFormat df = new SimpleDateFormat("HH:mm");
        String str = df.format(settingTime);
        Toast.makeText(this, str+" 에 알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();

        /*Intent intent=new Intent(ChangeAlarm.this, AlarmList.class);
        startActivity(intent);*/
        finish();
    }
    public void Cancel(View view) {
        /*Intent intent=new Intent(ChangeAlarm.this, AlarmList.class);
        startActivity(intent);*/
        finish();
    }
    public void Delete(View view) {
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent Intent = new Intent(this, RunAlarm.class);
        sql = dbHelper.getWritableDatabase();
        cursor = sql.rawQuery("SELECT * FROM Alarm;", null);
        if(cursor.getCount() > 0) {
            startManagingCursor(cursor);
            cursor.moveToPosition(position);
        }
        PendingIntent sender = PendingIntent.getActivity(this, cursor.getInt(0), Intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(sender);


        dbHelper.query("DELETE FROM Alarm WHERE _id='" + _id + "';");
        Toast.makeText(this, " 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        /*Intent intent=new Intent(ChangeAlarm.this, AlarmList.class);
        startActivity(intent);*/
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
