package com.internal.transmit;

import android.text.format.DateFormat;

public class Config {

    public static final boolean DEBUG = true;
    
    public static final String DEFAULT_NUM = "13810613108";
    
    public static String formatTime(long dateTaken) {
        return DateFormat.format("yyyy-MM-dd h:m:ss aa", dateTaken).toString();
    }
}
