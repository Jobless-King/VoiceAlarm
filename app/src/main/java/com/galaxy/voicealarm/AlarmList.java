package com.galaxy.voicealarm;

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

import java.util.ArrayList;
import java.util.StringTokenizer;

public class AlarmList extends FragmentActivity {

    RecyclerView listcore;
    DBHelper dbHelper;
    SQLiteDatabase sql;
    Cursor cursor;

    AlarmListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        listcore = (RecyclerView)findViewById(R.id.ListCore);
        dbHelper = DBHelper.getInstance();
        listAdapter = new AlarmListAdapter(this);
        listcore.setLayoutManager(new LinearLayoutManager(this));
        listcore.setAdapter(listAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        ArrayList<AlarmItem> temp = dbHelper.getAlarmItemListinDB();
        listAdapter.changeResources(temp);
    }

    public void RunAddAlarm(View view) {
        Intent intent = new Intent(AlarmList.this, AddAlarm.class);
        startActivity(intent);
        finish();
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
                    finish();
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
}