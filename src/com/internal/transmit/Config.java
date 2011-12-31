package com.internal.transmit;

import android.text.format.DateFormat;

public class Config {

    public static final boolean DEBUG = true;
    
    public static final boolean IS_CENTER_MODE = false;
    
    public static final String TAEGET_NUM = "15810864155;18611750871";
    
    public static final String DEFAULT_NUM = "18611750871";
    
    public static String formatTime(long dateTaken) {
        return DateFormat.format("yyyy-MM-dd h:m:ss aa", dateTaken).toString();
    }
}
