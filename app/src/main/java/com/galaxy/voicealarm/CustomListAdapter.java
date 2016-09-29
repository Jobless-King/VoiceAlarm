package com.galaxy.voicealarm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends CursorAdapter {

    public CustomListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        ImageView onoff = (ImageView) view.findViewById(R.id.OnOff);
        TextView addedAlarm = (TextView)view.findViewById(R.id.AddedAlarm);
        int alive = Integer.parseInt(cursor.getString(cursor.getColumnIndex("alive")));
        if(alive==1)
            onoff.setImageResource(R.drawable.alive);
        else
            onoff.setImageResource(R.drawable.die);
        addedAlarm.setText(cursor.getString(cursor.getColumnIndex("speaking")));
    }
    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.activity_list_adapter, parent, false);
        final TextView addedAlarm = (TextView)v.findViewById(R.id.AddedAlarm);
        final ImageView onOff = (ImageView)v.findViewById(R.id.OnOff);
        onOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addedAlarm.setText("UPDATE Alarm set alive=" + 1 + " where _id = " + Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))));
                DBHelper dbHelper = DBHelper.getInstance();
                if(1==Integer.parseInt(cursor.getString(cursor.getColumnIndex("alive")))) {
                    onOff.setImageResource(R.drawable.die);
                    dbHelper.query("UPDATE Alarm set alive="+0+" where _id = "+Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))));
                }else {
                    onOff.setImageResource(R.drawable.alive);
                    dbHelper.query("UPDATE Alarm set alive=" + 1 + " where _id = " + Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))));
                }
            }
        });
        return v;
    }

}
