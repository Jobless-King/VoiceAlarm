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
import android.media.MediaPlayer;
import android.net.Uri;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddAlarm extends AppCompatActivity {

    private Button outputTime;
    private ToggleButton mon, tue, wed, thu, fri, sat, sun;
    private RadioGroup selectedType;
    private EditText speaked;
    private AudioFile selectedAudioFile = new AudioFile("Sample", "Sample_Path");

    private int selectedHour, selectedMinute;
    static final int TIME_DIALOG_ID=1;

    //KFGD
    public static final int ADD_ALARM_AUDIO = 100;

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
        speaked = (EditText) findViewById(R.id.Speaked);

        ToogelOnClick();
        selectedType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.Auto:
                        speaked.setEnabled(false);
                        speaked.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittextoff));
                        break;
                    case R.id.Hand:
                        speaked.setEnabled(true);
                        speaked.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittexton));
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

    public void InputMusic(View view){
        Intent intent = new Intent(AddAlarm.this, SelectedAudioActivity.class);
        startActivityForResult(intent, ADD_ALARM_AUDIO);
    }

    public void PlayMusic(View v){
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
                    Toast.makeText(AddAlarm.this, selectedAudioFile.getFileName() + "mp3파일이 선택되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }
    public void Add(View view){
		DBHelper dbHelper = DBHelper.getInstance();
        int time = selectedHour*100+selectedMinute;
        int week = 0;
        String speaking = speaked.getText().toString();
        if(speaking.equals(""))
            speaking = "";
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
        String musicpath = selectedAudioFile.getFilePath();
        dbHelper.query("INSERT INTO Alarm VALUES (null, " + week + ", " + time + ", '" + speaking + "', '"+musicpath+"', 1);");

		SQLiteDatabase sql = dbHelper.getWritableDatabase();
        Cursor cursor = sql.rawQuery("SELECT _id FROM Alarm ORDER BY _id DESC;", null);
		cursor.moveToFirst();

        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent Intent = new Intent(this, RunAlarm.class);
        PendingIntent sender = PendingIntent.getActivity(this, cursor.getInt(0), Intent, PendingIntent.FLAG_CANCEL_CURRENT);
        long settingTime = System.currentTimeMillis() - ((System.currentTimeMillis()+9*60*60*1000)%(24*60*60*1000)) + selectedHour*60*60*1000 + selectedMinute*60*1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, settingTime, 24*60*60*1000,sender);

        DateFormat df = new SimpleDateFormat("HH:mm");
        String str = df.format(settingTime);
        Toast.makeText(this, str+" 에 알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();

        /*Intent intent=new Intent(AddAlarm.this, AlarmList.class);
        startActivity(intent);*/
        finish();
    }
    public void Cancel(View view) {
        /*Intent intent=new Intent(AddAlarm.this, MainActivity.class);
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

