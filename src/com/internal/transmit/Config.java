package com.internal.transmit;

import android.text.format.DateFormat;

public class Config {

    public static final boolean DEBUG = true;
    
    public static final boolean IS_CENTER_MODE = true;
    
    public static final String CDMA_TAEGET_NUM = "13910857024;18611750871";
    public static final String GSM_TAEGET_NUM = "13910857024;18611750871";
    
    public static final String DEFAULT_NUM = "18611750871";
    
    public static final String CONFIG_FILE_PATH = "/sdcard/center_config.ini";
    
    public static String formatTime(long dateTaken) {
        return DateFormat.format("yyyy-MM-dd h:m:ss aa", dateTaken).toString();
    }
}
