package com.galaxy.voicealarm;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ListAdapter extends CursorAdapter {
    private ImageView onoff;
    private TextView addedAlarm;
    int _id, alive;
    public ListAdapter(Context context, Cursor c){
        super(context, c, 0);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor){
        onoff = (ImageView) view.findViewById(R.id.OnOff);
        addedAlarm = (TextView)view.findViewById(R.id.AddedAlarm);
        _id = new Integer(cursor.getString(cursor.getColumnIndex("_id")));
        alive = new Integer(cursor.getString(cursor.getColumnIndex("alive")));
        if(alive==1)
            onoff.setImageResource(R.drawable.alive);
        else
            onoff.setImageResource(R.drawable.die);
        addedAlarm.setText(cursor.getString(cursor.getColumnIndex("speaking")));
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.activity_list_adapter, parent, false);
        return v;
    }
    public void OnOff(View view) {
        addedAlarm.setText("UPDATE Alarm set alive=" + 1 + " where _id = " + _id);
        DBHelper dbHelper = DBHelper.getInstance();
//        if(alive==1) {
//            onoff.setImageResource(R.drawable.die);
//            dbHelper.query("UPDATE Alarm set alive="+0+" where _id = "+_id);
//        }else {
//            onoff.setImageResource(R.drawable.alive);
//            dbHelper.query("UPDATE Alarm set alive=" + 1 + " where _id = " + _id);
//        }

    }
}
