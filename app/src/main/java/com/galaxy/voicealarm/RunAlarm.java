package com.galaxy.voicealarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.naverspeech.kfgd_naver.IManagerCommand;
import com.naver.naverspeech.kfgd_naver.NaverSpeechManager;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RunAlarm extends AppCompatActivity implements IManagerCommand {
    NaverSpeechManager naverSpeechManager;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private String voice, madeString;
    private TextView command, read, txt1, txt2;
    private DBHelper dbHelper;
    private SQLiteDatabase sql;
    private Cursor cursor;
    private int curTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_alarm);

        naverSpeechManager = NaverSpeechManager.CreateNaverSpeechManager(this, "54px6Qsc2zprZKsMMc4p", this);
        command = (TextView)findViewById(R.id.Command);
        read = (TextView)findViewById(R.id.Read);
        dbHelper = DBHelper.getInstance();
        sql = dbHelper.getWritableDatabase();
        cursor = sql.rawQuery("SELECT * FROM Alarm;", null);

        txt1 = (TextView)findViewById(R.id.txt1);
        txt2 = (TextView)findViewById(R.id.txt2);

        cursor = CurrentAlarmExist(cursor);
        if (cursor != null){
            RegisterNextAlarm(cursor);
            if(CurrentAlarmIsOn(cursor))
                RunCurrentAlarm();
            else
                this.finish();
        }else
            this.finish();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    @Override
    public void clientReady() {
        txt1.setText("Connected");
    }
    @Override
    public void audioRecording(short[] text) {}
    @Override
    public void partialResult(String partialText) {
        txt2.setText(partialText);
    }
    @Override
    public void finalResult(String[] finalText) {
        txt2.setText(finalText[0]);
        voice = finalText[0];
    }
    @Override
    public void recognitionError(String errorText) {
        txt1.setText("erro : " + errorText);
    }
    @Override
    public void clientInactive() {
        Check();
    }
    public void MicOn(View view){
        if (!naverSpeechManager.getRecognizeState()) {
            // Start button is pushed when SpeechRecognizer's state is inactive.
            // Run SpeechRecongizer by calling recognize().
            txt2.setText("");
            naverSpeechManager.startRecognize();
        } else {
            // This flow is occurred by pushing start button again
            // when SpeechRecognizer is running.
            // Because it means that a user wants to cancel speech
            // recognition commonly, so call stop().
            naverSpeechManager.stopRecognize();
        }
    }

    public void Kill(View view){
        mediaPlayer.stop();
        vibrator.cancel();
        finish();
    }
    @Override
    public void onBackPressed(){
        Toast.makeText(this, "안되 아니야 못꺼 자지마 일어나", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        naverSpeechManager.initSpeechRecognizer();
    }

    @Override
    protected void onPause(){
        super.onPause();
        naverSpeechManager.releaseRecognizer();
    }
    private Cursor CurrentAlarmExist(Cursor cursor){
        boolean isRun = false;
        SimpleDateFormat df = new SimpleDateFormat("HHmm", Locale.KOREA);
        curTime = Integer.parseInt(df.format(new Date()));

        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            while(!cursor.isAfterLast()) {
                if (curTime == cursor.getInt(cursor.getColumnIndex("time")))
                    return cursor;
                cursor.moveToNext();
            }
        }
        return null;
    }
    private void RegisterNextAlarm(Cursor cursor){
        int selectedHour, selectedMinute;
        selectedHour = curTime/100;
        selectedMinute = curTime%100;
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent Intent = new Intent(this, RunAlarm.class);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, Intent, 0);
        long settingTime = System.currentTimeMillis() - ((System.currentTimeMillis()+9*60*60*1000)%(24*60*60*1000)) + selectedHour*60*60*1000 + selectedMinute*60*1000;
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, settingTime, 24*60*60*1000, pIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP,settingTime+(24*60*60*1000), pIntent);
    }
    private boolean CurrentAlarmIsOn(Cursor cursor){
        if(1!=cursor.getInt(cursor.getColumnIndex("alive")))
            return false;
        int week = cursor.getInt(cursor.getColumnIndex("week"));
        Calendar calendar = Calendar.getInstance();
        int curWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch(curWeek){
            case 1:
                week = week/1000000;
                break;
            case 2:
                week = week%10;
                break;
            case 3:
                week = (week/10)%10;
                break;
            case 4:
                week = (week/100)%10;
                break;
            case 5:
                week = (week/1000)%10;
                break;
            case 6:
                week = (week/10000)%10;
                break;
            case 7:
                week = (week/100000)%10;
                break;
        }
        if(week!=1)
            return false;
        return true;
    }
    private void RunCurrentAlarm() {

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String selected = mSimpleDateFormat.format(new Date());
        Memo memo = dbHelper.getMemoListFromDB().get(selected);

        if (memo != null) {
            //일정이 있을때
            madeString = memo.getContent();
            command.setText("오늘 할일은?");
            read.setText(memo.getContent());
        } else {
            madeString = ("일찍 일어난 벌레");
            command.setText("할일은 없지만 일어나렴");
            read.setText("일찍 일어난 벌레");
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.escape);
        mediaPlayer.setLooping(true);
        //mediaPlayer.start();
        long[] pattern = { 0, 500, 200, 400, 100 };
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, 2);
    }
    private void Check(){
        txt1.setText("Connect end");
        if(voice.equals(madeString)) {
            Kill(null);
        }
    }
}
