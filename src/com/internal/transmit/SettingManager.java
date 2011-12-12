package com.internal.transmit;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingManager {
    private static final String TAG = "SettingManager";
    
    private static SettingManager gSettingManager;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    
    public static SettingManager getInstance() {
        if (gSettingManager == null) {
            gSettingManager = new SettingManager();
        }
        return gSettingManager;
    }
    
    public void init(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
    }
    
    public String getPhoneNum() {
        return mSharedPreferences.getString(mContext.getString(R.string.pref_phone_num), Config.DEFAULT_NUM);
    }
    
    public void setPhoneNum(String phone) {
        mEditor.putString(mContext.getString(R.string.pref_phone_num), phone);
        mEditor.commit();
    }
}
