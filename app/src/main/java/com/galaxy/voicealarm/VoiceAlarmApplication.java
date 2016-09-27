package com.galaxy.voicealarm;

import android.app.Application;
import android.content.Context;

import java.util.HashMap;

/**
 * Created by GwanYongKim on 2016-09-27.
 */

public class VoiceAlarmApplication  extends Application{
    private static Context context;
    private static HashMap<String, Memo> memoList;

    @Override
    public void onCreate(){
        super.onCreate();
        context = this;
        settingMemoList();
    }

    private void settingMemoList(){
        memoList = DBHelper.getInstance().getMemoListFromDB();
    }

    public static Memo getMemo(String datetime){
        return memoList.get(datetime);
    }

    public static Context getVoiceAlarmContext(){
        return context;
    }
}
