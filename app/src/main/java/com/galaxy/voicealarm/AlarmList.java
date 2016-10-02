package com.galaxy.voicealarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class AlarmList extends FragmentActivity {

    private int curtime;
    RecyclerView listcore;
    DBHelper dbHelper;
    SQLiteDatabase sql;
    Cursor cursor;
    AlarmListAdapter listAdapter;

    static  final int NAPNOTI = 1;
    NotificationManager mNotiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        listcore = (RecyclerView)findViewById(R.id.ListCore);
        dbHelper = DBHelper.getInstance();
        listAdapter = new AlarmListAdapter(this);
        listcore.setLayoutManager(new LinearLayoutManager(this));
        listcore.setAdapter(listAdapter);

        mNotiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    protected void onResume(){
        super.onResume();
        ArrayList<AlarmItem> temp = dbHelper.getAlarmItemListinDB();
        listAdapter.changeResources(temp);

        sql = dbHelper.getReadableDatabase();
        cursor = sql.rawQuery("SELECT * FROM Alarm ORDER BY time", null);
        cursor.moveToFirst();
        cursor = MostFastAlarmAfterNow(cursor);
        if(cursor!=null){
            Toast.makeText(this, "있다", Toast.LENGTH_SHORT).show();
            //setNotification(cursor);
        }else{
            Toast.makeText(this, "없다", Toast.LENGTH_SHORT).show();
        }
    }

    public void RunAddAlarm(View view) {
        Intent intent = new Intent(AlarmList.this, AddAlarm.class);
        startActivity(intent);
        //finish();
    }

    public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder>{

        private Context mContext;
        private ArrayList<AlarmItem> mResources;

        public AlarmListAdapter(Context context){
            this.mContext = context;
            this.mResources = new ArrayList<>();
        }

        public void changeResources(ArrayList<AlarmItem> newResources){
            this.mResources  = newResources;
            notifyDataSetChanged();
        }

        @Override
        public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_adapter, parent, false);
            AlarmViewHolder holder = new AlarmViewHolder(itemRoot);
            return holder;
        }

        @Override
        public int getItemCount() {
            return mResources.size();
        }

        @Override
        public void onBindViewHolder(final AlarmViewHolder holder, int position) {
            final AlarmItem item = mResources.get(position);
            final int pos = position;

            if(1 == item.alive){
                holder.imageView.setImageResource(R.drawable.alive);
                int color = Color.parseColor("#EE3C45");
                holder.textView.setTextColor(color);
            }else{
                holder.imageView.setImageResource(R.drawable.die);
                int color = Color.parseColor("#918DA7");
                holder.textView.setTextColor(color);
            }
            Log.i("info", String.valueOf(item.time));
            int hour = item.time / 100;
            int min = item.time - hour * 100;

            if(12 <= hour){
                if (0 == min) {
                    holder.textView.setText("PM "+String.valueOf(hour-12) + ":00");
                } else{
                    holder.textView.setText("PM "+String.valueOf(hour-12) + ":"+String.valueOf(min));
                }
            }else {
                if (0 == min) {
                    holder.textView.setText("AM " + String.valueOf(hour) + ":00");
                } else {
                    holder.textView.setText("AM " + String.valueOf(hour) + ":" + String.valueOf(min));
                }
            }

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(1 == item.alive){
                        dbHelper.query("UPDATE Alarm set alive ="+0+" where _id = " + item._id);
                    }else{
                        dbHelper.query("UPDATE Alarm set alive ="+1+" where _id = " + item._id);
                    }
                    changeResources(dbHelper.getAlarmItemListinDB());
                }
            });

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AlarmList.this, ChangeAlarm.class);
                    intent.putExtra("position", pos);
                    startActivity(intent);
                    //finish();
                }
            });
        }

        public class AlarmViewHolder extends RecyclerView.ViewHolder {

            public final ImageView imageView;
            public final TextView textView;

            public AlarmViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView)itemView.findViewById(R.id.OnOff);
                textView = (TextView)itemView.findViewById(R.id.AddedAlarm);
            }
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

        int weekToInt = cursor.getInt(cursor.getColumnIndex("week"));
        int time = cursor.getInt(cursor.getColumnIndex("time"));
        int[] flags = new int[]{0, 0, 0, 0, 0, 0, 0};
        for(int i=0; i<=6; ++i){    //0:Monday-6:Sunday
            int divide = (int) Math.pow(10, i);
            if(1==(weekToInt/divide)%10){
                flags[i]  = 1;
            }
        }

        int valueOfTemp = flags[0];
        for(int i=1; i<=6; ++i){
            int temp = flags[i];
            flags[i] = valueOfTemp;
            valueOfTemp = temp;
        }
        flags[0] = valueOfTemp;


        Calendar calendar = Calendar.getInstance();
        int curWeek = calendar.get(Calendar.DAY_OF_WEEK);   //1=Sunday, 2=Monday

        //1, 0, 1, 0, 1, 0, 0
        //0, 1, 0, 1, 0, 1, 0

        String weekToString = "";

        switch (weekToInt){

        }

        Intent intent = new Intent(AlarmList.this, AlarmList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent content = PendingIntent.getActivity(
                AlarmList.this, 0, intent, 0
        );
        Notification noti = new Notification.Builder(AlarmList.this)
                .setContentTitle("알람 설정")
                .setContentText("알람이 설정되었습니다.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(content)
                .setOngoing(true)
                .getNotification();
        mNotiManager.notify(AlarmList.NAPNOTI, noti);

    }
}