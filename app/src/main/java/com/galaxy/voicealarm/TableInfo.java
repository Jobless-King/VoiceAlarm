package com.galaxy.voicealarm;

import android.provider.BaseColumns;

/**
 * Created by GwanYongKim on 2016-09-27.
 */

public final class TableInfo {
    public static final class SCHEDULE implements BaseColumns{
        public static final String TABLE_NAME = "Schedule";
        public static final String _ID = "_id";
        public static final String DATE_TIME = "datetime";
        public static final String CONTENT = "content";
    }
}
