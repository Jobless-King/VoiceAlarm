package com.galaxy.voicealarm;

/**
 * Created by GwanYongKim on 2016-09-29.
 */

public class AlarmItem{
    public int _id;
    public int week;
    public int time;
    public String speaking;
    public int alive;

    public AlarmItem(int _id, int week, int time, String speaking, int alive){
        this._id = _id;
        this.week = week;
        this.time = time;
        this.speaking = speaking;
        this.alive = alive;
    }
}
