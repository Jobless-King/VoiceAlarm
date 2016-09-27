package com.galaxy.voicealarm;

import android.app.Application;
import android.content.Context;

/**
 * Created by GwanYongKim on 2016-09-27.
 */

public class VoiceAlarmApplication  extends Application{
    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        context = this;
    }

    public static Context getVoiceAlarmContext(){
        return context;
    }
}
