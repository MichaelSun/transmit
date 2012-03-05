package com.internal.transmit.utils;

import android.text.format.DateFormat;

public class Config {

    public static final boolean DEBUG = true;
    
    public static final boolean NEED_ENCRYPT = true;
    
    public static final String CONFIG_FILE_PATH = "/sdcard/Encrypt_SFS_Config.ini";
    
    public static final String SECTION_CENTER = "INFO";
    public static final String PROPERTY_CENTER = "is_center";
    public static final String PROPERTY_TARGET = "default_target";
    public static final String PROPERTY_ENTER_PASSWORD = "password";
    public static final String PROPERTY_IMEI = "imei";
    public static final String PROPERTY_IMSI = "imsi";
    public static final String PROPERTY_SEND = "send_enable";
    
    public static final String SECTION_GSM = "GSM";
    public static final String SECTION_CDMA = "CDMA";
    public static final String PROPERTY_NUMLIST = "numlist";
    
    public static final String BROADCAST_ACTION = "com.internal.transmit.reload";
    
    public static String formatTime(long dateTaken) {
        return DateFormat.format("yyyy-MM-dd h:m:ss aa", dateTaken).toString();
    }
}
