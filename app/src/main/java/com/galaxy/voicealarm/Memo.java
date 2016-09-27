package com.galaxy.voicealarm;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GwanYongKim on 2016-09-27.
 */

public class Memo implements Parcelable{
    private final int _id; //PrimaryKey From DB
    private final String date_time;
    private String content;

    public Memo(String DATE_TIME){
        this._id = -1;
        this.date_time = DATE_TIME;
    }

    public Memo(int _ID, String DATE_TIME, String MEMO_TEST){
        this._id = _ID;
        this.date_time = DATE_TIME;
        this.content = MEMO_TEST;
    }

    protected Memo(Parcel source){
        _id = source.readInt();
        date_time = source.readString();
        content = source.readString();
    }

    public Date getDataTimeToDate(){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(date_time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getID(){return _id;}

    public String getDataTimeToString(){
        return date_time;
    }

    public String getContent(){return content;}

    public void setContent(String content){
        this.content = content;
    }

    public static final Creator<Memo> CREATOR = new Creator<Memo>() {
        @Override
        public Memo createFromParcel(Parcel source) {
            return new Memo(source);
        }

        @Override
        public Memo[] newArray(int size) {
            return new Memo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(date_time);
        dest.writeString(content);
    }
}
