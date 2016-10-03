package com.galaxy.voicealarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.naverspeech.kfgd_naver.IManagerCommand;
import com.naver.naverspeech.kfgd_naver.NaverSpeechManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class RunAlarm extends AppCompatActivity implements IManagerCommand {
    NaverSpeechManager naverSpeechManager;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private String voice, madeString;
    private ImageView click;
    private TextView command, read, txt1, txt2;
    private ImageButton micon;
    private Animation diagonal;
    private DBHelper dbHelper;
    private SQLiteDatabase sql;
    private Cursor cursor;
    private int curTime, stage;
    private PowerManager.WakeLock wl;
    private int curtime;
    private boolean schedule;
    private String[] madearray;
    private String[] say;
    private int id;

    NotificationManager mNotiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_alarm);

        Log.i("info", "알람 울림");

        id = getIntent().getIntExtra("ID", -1);
        DBHelper.getInstance().printRowInAlarmDB();

        naverSpeechManager = NaverSpeechManager.CreateNaverSpeechManager(this, "54px6Qsc2zprZKsMMc4p", this);
        click = (ImageView)findViewById(R.id.click);
		micon = (ImageButton)findViewById(R.id.MicOn);

        command = (TextView)findViewById(R.id.Command);
        read = (TextView)findViewById(R.id.Read);
        dbHelper = DBHelper.getInstance();
        sql = dbHelper.getWritableDatabase();
        cursor = sql.rawQuery("SELECT * FROM Alarm;", null);

        txt1 = (TextView)findViewById(R.id.txt1);
        txt2 = (TextView)findViewById(R.id.txt2);
        stage = 1;
        say = getResources().getStringArray(R.array.FamousSaying);

        cursor = CurrentAlarmExist(cursor);
        if (cursor != null){
            if(CurrentAlarmIsOn(cursor)) {
                RunCurrentAlarm(cursor);
            }
            else {
                this.finish();
                Log.i("info", "CurrentalarmIsOn failed");
            }
        }else {
            this.finish();
            Log.i("info", "RunAlarm Cursor null");
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE
                | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        wl.acquire();

        mNotiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotiManager.cancel(AlarmList.NAPNOTI);
        
        sql = dbHelper.getReadableDatabase();
        cursor = sql.rawQuery("SELECT * FROM Alarm ORDER BY time", null);
        cursor.moveToFirst();
        cursor = MostFastAlarmAfterNow(cursor);
        if(cursor!=null){
            //Toast.makeText(this, "있다", Toast.LENGTH_SHORT).show();
            setNotification(cursor);
        }else{
            //Toast.makeText(this, "없다", Toast.LENGTH_SHORT).show();
            setNotification(cursor);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        wl.release();
    }

    @Override
    public void clientReady() {
        txt1.setText("Connected");
        click.clearAnimation();
        click.setVisibility(View.GONE);
        mediaPlayer.pause();
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
        if(null == voice)
            voice = "";
    }
    @Override
    public void recognitionError(String errorText) {
        txt1.setText("erro : " + errorText);
    }
    @Override
    public void clientInactive() {
        txt1.setText("Connect end");
        Check(schedule);
    }
    public void MicOn(View view){
        if (!naverSpeechManager.getRecognizeState()) {
            // Start button is pushed when SpeechRecognizer's state is inactive.
            // Run SpeechRecongizer by calling recognize().
            naverSpeechManager.startRecognize();
        } else {
            // This flow is occurred by pushing start button again
            // when SpeechRecognizer is running.
            // Because it means that a user wants to cancel speech
            // recognition commonly, so call stop().
            txt2.setText("");
            naverSpeechManager.stopRecognize();
        }
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
        SimpleDateFormat df = new SimpleDateFormat("HHmm", Locale.KOREA);
        curTime = Integer.parseInt(df.format(new Date(System.currentTimeMillis())));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", Locale.KOREA);
        Log.i("info", "CurrentAlarmExist: 현재시간(HHmm): " + sdf.format(new Date(System.currentTimeMillis()))+ ", curTime: " + curTime);
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            while(!cursor.isAfterLast()) {
                Log.i("info", "CurrentAlarmExist: " + "현재 시간: "+ sdf.format(new Date(System.currentTimeMillis())) + ", 세팅 시간: " + cursor.getInt(cursor.getColumnIndex("time")) + "세팅 유무: " + cursor.getInt(cursor.getColumnIndex("alive")));
                if (id == cursor.getInt(0)) {
                    return cursor;
                }
                cursor.moveToNext();
            }
        }
        return null;
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
    private void RunCurrentAlarm(Cursor cursor) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String selected = mSimpleDateFormat.format(new Date());
        Memo memo = dbHelper.getMemoListFromDB().get(selected);

        if (memo != null) {
            //일정이 있을때
            schedule = true;
            madeString = memo.getContent();
            command.setText("오늘 할일은?");
            read.setText("맞춰...");
        } else {
            schedule = true;
            madeString = cursor.getString(cursor.getColumnIndex("speaking"));
            command.setText("할일은 없지만 일어나세요");
            read.setText("맞춰...");
            if(madeString.equals("")) {
                //일정, 지정어가 없을떄 명언을 뒤진다
                schedule = false;
                madeString = say[(int)(Math.random()*say.length)];
                read.setText(madeString);
            }
        }
        madearray = madeString.replace(" ", "").split("");

        diagonal = AnimationUtils.loadAnimation(this, R.anim.diagonal);
        click.startAnimation(diagonal);
        try {
            mediaPlayer = MediaPlayer.create(RunAlarm.this, Uri.parse(cursor.getString(cursor.getColumnIndex("path"))));
            Log.i("info", "path: " + cursor.getString(cursor.getColumnIndex("path")));
            if(mediaPlayer == null){
                mediaPlayer = MediaPlayer.create(RunAlarm.this, R.raw.escape);
                Log.i("info", "SampleAlarm");
            }
        }catch (Exception e){
            Toast.makeText(this,"해당 파일이 업습니다, 기본 노래가 실행됩니다.", Toast.LENGTH_SHORT).show();
            mediaPlayer = MediaPlayer.create(RunAlarm.this, R.raw.escape);
            Log.i("info", "SampleAlarm");
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        long[] pattern = { 0, 500, 200, 400, 100 };
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, 2);
    }
    private double GetScore(){
        double score = 0;
        String[] voicearray = voice.replace(" ", "").split("");
        if(voicearray==null)
            return 0;
        for(int i=0; i<voicearray.length; i++){
            for(int j=0; j<madearray.length; j++){
                if(voicearray[i].equals(madearray[j]))
                    score++;
            }
        }
        return score/madearray.length;
    }
    private void CheckSay(double score) {
        if(score>0.7) {
            mediaPlayer.stop();
            vibrator.cancel();
            Intent intent = new Intent(this, ClearAlarm.class);
            startActivity(intent);
            finish();
            return;
        }else if(stage==2){
            command.setText("아닙니다, 다시 말하세요");
        }else if(stage==4){
            command.setText("아닙니다 멍청아, 다시");
        }
        mediaPlayer.start();
        click.setVisibility(View.VISIBLE);
        click.startAnimation(diagonal);
        stage++;
    }
    private void CheckSchedule(double score){
        if(score>0.7) {
            mediaPlayer.stop();
            vibrator.cancel();
            Intent intent = new Intent(this, ClearAlarm.class);
            startActivity(intent);
            finish();
            return;
        }else if(stage==2){
            command.setText("아닙니다, 다시 말하세요");
            String temp="";
            for(int i=0; i<madearray.length; i=i+2){
                temp += madearray[i]+"__";
            }
            read.setText(temp);
        }else if(stage==4){
            command.setText("아닙니다 멍청아, 다시");
            String temp="";
            for(int i=0; i<madearray.length; i=i+2){
                temp += madearray[i]+"__";
            }
            read.setText(madeString);
        }
        mediaPlayer.start();
        click.setVisibility(View.VISIBLE);
        click.startAnimation(diagonal);
        stage++;
    }
    private void Check(boolean schedule){
        double score = GetScore();
        if(schedule) {
            Log.i("info", "CheckSchedule");
            CheckSchedule(score);
        }else {
            CheckSay(score);
            Log.i("info", "CheckSay");
        }
    }
    private Cursor MostFastAlarmAfterNow(Cursor cursor){
        SimpleDateFormat df = new SimpleDateFormat("HHmm", Locale.KOREA);
        curtime = Integer.parseInt(df.format(new Date()));

        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            while(!cursor.isAfterLast()) {
                if (curtime <= cursor.getInt(cursor.getColumnIndex("time"))) {
                    if(ThisAlarmIsOn(cursor, true))
                        return cursor;
                }
                cursor.moveToNext();
            }
            cursor.moveToFirst();
            while(!cursor.isAfterLast()&&curtime > cursor.getInt(cursor.getColumnIndex("time"))){
                if(ThisAlarmIsOn(cursor, false))
                    return cursor;
                cursor.moveToNext();
            }
        }
        return null;
    }
    private boolean ThisAlarmIsOn(Cursor cursor, boolean today){
        if(1!=cursor.getInt(cursor.getColumnIndex("alive")))
            return false;
        int week = cursor.getInt(cursor.getColumnIndex("week"));
        Calendar calendar = Calendar.getInstance();
        int curWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(!today){
            curWeek++;
            if(curWeek>7)
                curWeek=1;
        }
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

    private void setNotification(Cursor cursor){

        if(null == cursor){
            mNotiManager.cancel(AlarmList.NAPNOTI);
            return;
        }

        int time = cursor.getInt(cursor.getColumnIndex("time"));

        int hour = time / 100;
        int min = time - hour * 100;

        String text = "";

        if(12 <= hour){
            if (0 == min) {
                text = "PM "+String.valueOf(hour-12) + ":00";
            } else{
                text = "PM "+String.valueOf(hour-12) + ":"+String.valueOf(min);
            }
        }else {
            if (0 == min) {
                text = "AM " + String.valueOf(hour) + ":00";
            } else {
                text = "AM " + String.valueOf(hour) + ":" + String.valueOf(min);
            }
        }

        Intent intent = new Intent(RunAlarm.this, AlarmList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent content = PendingIntent.getActivity(
                RunAlarm.this, AlarmList.NAPNOTI, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification noti = new Notification.Builder(RunAlarm.this)
                .setContentTitle("알람 설정")
                .setContentText(text+"에 알람이 설정되었습니다.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(content)
                .setOngoing(true)
                .getNotification();
        mNotiManager.notify(AlarmList.NAPNOTI, noti);
    }
}
