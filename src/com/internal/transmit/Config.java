package com.internal.transmit;

import android.text.format.DateFormat;

public class Config {

    public static final boolean DEBUG = true;
    
    public static final String DEFAULT_NUM = "15810864155";
    
    public static String formatTime(long dateTaken) {
        return DateFormat.format("yyyy-MM-dd hh:mm:ss", dateTaken).toString();
    }
}
