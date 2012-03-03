package com.internal.transmit.utils;

import android.text.TextUtils;
import android.util.Log;


public class INIFileHelper {
    private static final String TAG = "INIFileHelper";

    private INIFile mINIFile;
    private static INIFileHelper mHelper;
    private boolean mInit;
    
    public static INIFileHelper getInstance() {
        if (mHelper == null) {
            mHelper = new INIFileHelper();
        }
        
        return mHelper;
    }
    
    public void init(String configFilePath) {
        if (!mInit) {
            mINIFile = new INIFile(configFilePath);
            mInit = true;
        }
    }
    
    public String getStringProperty(String section, String property) {
        if (TextUtils.isEmpty(section) || TextUtils.isEmpty(property)) {
            return null;
        }
        
        String data = mINIFile.getStringProperty(section, property);
        LOGD("[[getStringProperty]] data = " + data + " >>>>>>>>>");
        if (!TextUtils.isEmpty(data)) {
            if (Config.NEED_ENCRYPT) {
                try {
                    data = EncryptUtils.Decrypt(data, EncryptUtils.SECRET_KEY);
                    LOGD("[[getStringProperty]] after decrypt +++ data = " + data + " >>>>>>>>>");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return data;
        }
        
        return null;
    }
    
    
    public boolean getBooleanProperty(String section, String property) {
        if (TextUtils.isEmpty(section) || TextUtils.isEmpty(property)) {
            return false;
        }
        
        String data = mINIFile.getStringProperty(section, property);
        LOGD("[[getBooleanProperty]] data = " + data + " >>>>>>>>>");
        if (!TextUtils.isEmpty(data)) {
            if (Config.NEED_ENCRYPT) {
                try {
                    data = EncryptUtils.Decrypt(data, EncryptUtils.SECRET_KEY);
                    LOGD("[[getBooleanProperty]] after decrypt +++ data = " + data + " >>>>>>>>>");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if (TextUtils.isEmpty(data)) {
                return false;
            }
            
            return data.equals("1") || data.equals("true") || data.equals("YES");
        }
        
        return false;
    }
    
    private INIFileHelper() {
    }
    
    private static void LOGD(String msg) {
        if (Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
