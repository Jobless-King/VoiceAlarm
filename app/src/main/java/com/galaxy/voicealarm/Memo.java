package com.galaxy.voicealarm;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GwanYongKim on 2016-09-27.
 */

public class Memo {
    private int _id; //PrimaryKey From DB
    private String date_time;
    private String memo_text;

    public Memo(String DATE_TIME){
        this.date_time = DATE_TIME;
    }

    public Memo(int _ID, String DATE_TIME, String MEMO_TEST){
        this._id = _ID;
        this.date_time = DATE_TIME;
        this.memo_text = MEMO_TEST;
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

    public String getDataTimeToString(){
        return date_time;
    }
}
