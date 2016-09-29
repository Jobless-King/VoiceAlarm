package com.galaxy.voicealarm;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Debug;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper{

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "VoiceAlarm_schema.db";
    private static DBHelper dbHelper;

    private DBHelper(){
        super(VoiceAlarmApplication.getVoiceAlarmContext(), DB_NAME, null, DB_VERSION);
    }

   public DBHelper(Context context, String DBName){
        super(context, DBName, null, 1);
   }  //수정: KFGD

    private DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    public static DBHelper getInstance(){
        if(null == dbHelper)
            dbHelper = new DBHelper();
        return dbHelper;
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE Schedule(_id INTEGER PRIMARY KEY AUTOINCREMENT, datetime TEXT, content TEXT)");
        db.execSQL("CREATE TABLE Alarm(_id INTEGER PRIMARY KEY AUTOINCREMENT, week INTEGER, time INTEGER, speaking TEXT, alive INTEGER)");
    }
    public void onUpgrade(SQLiteDatabase db, int oloVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS Schedule");
        db.execSQL("DROP TABLE IF EXISTS Alarm");
        onCreate(db);
    }
    public void query(String query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }

    public HashMap<String, Memo> getMemoListFromDB(){
        HashMap<String, Memo> memoList = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            //String[] columnNames = {"datetime", "content"};
            db = getReadableDatabase();
            cursor = db.query("Schedule", null, null, null, null, null, null);
            memoList = new HashMap<>();
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                int _ID = cursor.getInt(cursor.getColumnIndex(TableInfo.SCHEDULE._ID));
                String DATE_TIME = cursor.getString(cursor.getColumnIndex(TableInfo.SCHEDULE.DATE_TIME));
                String CONTENT = cursor.getString(cursor.getColumnIndex(TableInfo.SCHEDULE.CONTENT));
                memoList.put(DATE_TIME, new Memo(_ID, DATE_TIME, CONTENT));
                cursor.moveToNext();
            }
        } catch (Exception e){
            Log.e("getMemoListFromDB", e.toString());
        } finally{
            close(db, cursor);
        }
        return memoList;
    }

    public void insertMemoinDB(Memo memo){
        SQLiteDatabase db = null;
        try{
            db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.SCHEDULE.DATE_TIME, memo.getDataTimeToString());
            contentValues.put(TableInfo.SCHEDULE.CONTENT, memo.getContent());
            db.insert(TableInfo.SCHEDULE.TABLE_NAME, null, contentValues);
        } catch (Exception e){
            Log.e("getMemoListFromDB", e.toString());
        } finally{
            close(db);
        }
    }

    public void updateMemoinDB(Memo memo){
        SQLiteDatabase db = null;
        try{
            db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.SCHEDULE.CONTENT, memo.getContent());
            String[] params = {String.valueOf(memo.getID())};
            db.update(TableInfo.SCHEDULE.TABLE_NAME,
                    contentValues,
                    TableInfo.SCHEDULE._ID + "=?",
                    params);
        } catch (Exception e){
            Log.e("getMemoListFromDB", e.toString());
        } finally{
            close(db);
        }
    }

    public void deleteMemoinDB(Memo memo){
        SQLiteDatabase db = null;
        try{
            db = getWritableDatabase();
            db.delete(TableInfo.SCHEDULE.TABLE_NAME, TableInfo.SCHEDULE._ID + "=?", new String[]{String.valueOf(memo.getID())});
        }catch (Exception e){
            e.printStackTrace();
            Log.e("error", "deleteMemoinDB Error");
        }finally {
            close(db);
        }
    }

    public ArrayList<AlarmItem> getAlarmItemListinDB(){
        ArrayList<AlarmItem> alarmItems = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        /*(_id INTEGER PRIMARY KEY AUTOINCREMENT, week INTEGER, time INTEGER, speaking TEXT, alive INTEGER)*/
        try{
            //String[] columnNames = {"datetime", "content"};
            db = getReadableDatabase();
            cursor = db.query("Alarm", null, null, null, null, null, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                int _ID = cursor.getInt(cursor.getColumnIndex("_id"));
                int WEEK = cursor.getInt(cursor.getColumnIndex("week"));
                int TIME = cursor.getInt(cursor.getColumnIndex("time"));
                String SPEACKING = cursor.getString(cursor.getColumnIndex("speaking"));
                int ALIVE = cursor.getInt(cursor.getColumnIndex("alive"));
                alarmItems.add(new AlarmItem(_ID, WEEK, TIME, SPEACKING, ALIVE));
                cursor.moveToNext();
            }
        } catch (Exception e){
            Log.e("getAlarmItemListFromDB", e.toString());
        } finally{
            close(db, cursor);
        }
        return alarmItems;
    }

    private void close(SQLiteDatabase db){
        if(null != db)
            db.close();
    }

    private void close(SQLiteDatabase db, Cursor cursor){
        if(null != cursor)
            cursor.close();
        if(null != db)
            db.close();
    }
}
