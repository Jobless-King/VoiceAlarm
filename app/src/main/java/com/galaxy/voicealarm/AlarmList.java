package com.galaxy.voicealarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class AlarmList extends FragmentActivity {

    ListView listcore;
    DBHelper dbHelper;
    SQLiteDatabase sql;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        listcore = (ListView)findViewById(R.id.ListCore);
        dbHelper = new DBHelper(getApplicationContext(), "Alarm");
        sql = dbHelper.getWritableDatabase();
        cursor = sql.rawQuery("SELECT * FROM Alarm;", null);
        if(cursor.getCount() > 0){
            startManagingCursor(cursor);
            ListAdapter dbAdapter = new ListAdapter(this, cursor);
            listcore.setAdapter(dbAdapter);
        }
        listcore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AlarmList.this, position, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AlarmList.this, AlarmList.class);
                intent.putExtra("id", position);
                startActivity(intent);
                finish();
            }
        });
    }

    public void RunAddAlarm(View view){
        Intent intent=new Intent(AlarmList.this, AddAlarm.class);
        startActivity(intent);
        finish();
    }
}
