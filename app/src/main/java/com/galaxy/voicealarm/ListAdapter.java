package com.galaxy.voicealarm;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ListAdapter extends CursorAdapter {
    public ListAdapter(Context context, Cursor c){
        super(context, c, 0);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor){
        final TextView schedule = (TextView)view.findViewById(R.id.AddedAlarm);
        schedule.setText(cursor.getString(cursor.getColumnIndex("time"))+"\n"+cursor.getString(cursor.getColumnIndex("speaking")));
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.activity_list_adapter, parent, false);
        return v;
    }
}
