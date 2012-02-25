package com.internal.transmit;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class SettingManager {
    private static final String TAG = "SettingManager";
    
    private static SettingManager gSettingManager;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private INIFile mINIFile = new INIFile(Config.CONFIG_FILE_PATH);
    
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
    
    public ArrayList<String> getCDMATargetList() {
//        String targetList = mSharedPreferences.getString(mContext.getString(R.string.pref_target_list_cdma), Config.CDMA_TAEGET_NUM);
//        if (!TextUtils.isEmpty(targetList)) {
//            ArrayList<String> ret = new ArrayList<String>();
//            String[] targetArray = targetList.split(";");
//            for (String target : targetArray) {
//                ret.add(target);
//            }
//            
//            return ret;
//        }
//        
//        return null;
        
        if (mINIFile != null) {
            String targets = mINIFile.getStringProperty("CDMA", "numlist");
            if (!TextUtils.isEmpty(targets)) {
                ArrayList<String> ret = new ArrayList<String>();
                String[] targetArray = targets.split(";");
                for (String target : targetArray) {
                    if (!TextUtils.isEmpty(target)) {
                        ret.add(target);
                    }
                }
                
                return ret;
            }
        }
        
        return null;
    }
    
    public void setCDMATargetList(ArrayList<String> targetList) {
//        if (targetList != null) {
//            StringBuffer saved = new StringBuffer();
//            for (String target : targetList) {
//                saved.append(target).append(";");
//            }
//            if (saved.length() > 0) {
//                mEditor.putString(mContext.getString(R.string.pref_target_list_cdma)
//                            , saved.substring(0, saved.length() - 1));
//                mEditor.commit();
//            }
//        }
    }
    
    public ArrayList<String> getGSMTargetList() {
//        String targetList = mSharedPreferences.getString(mContext.getString(R.string.pref_target_list_gsm), Config.GSM_TAEGET_NUM);
//        if (!TextUtils.isEmpty(targetList)) {
//            ArrayList<String> ret = new ArrayList<String>();
//            String[] targetArray = targetList.split(";");
//            for (String target : targetArray) {
//                ret.add(target);
//            }
//            
//            return ret;
//        }
//        
//        return null;
        
        if (mINIFile != null) {
            String targets = mINIFile.getStringProperty("GSM", "numlist");
            if (!TextUtils.isEmpty(targets)) {
                ArrayList<String> ret = new ArrayList<String>();
                String[] targetArray = targets.split(";");
                for (String target : targetArray) {
                    if (!TextUtils.isEmpty(target)) {
                        ret.add(target);
                    }
                }
                
                return ret;
            }
        }
        
        return null;
    }
    
    public void setGSMTargetList(ArrayList<String> targetList) {
//        if (targetList != null) {
//            StringBuffer saved = new StringBuffer();
//            for (String target : targetList) {
//                saved.append(target).append(";");
//            }
//            if (saved.length() > 0) {
//                mEditor.putString(mContext.getString(R.string.pref_target_list_gsm)
//                            , saved.substring(0, saved.length() - 1));
//                mEditor.commit();
//            }
//        }
    }
    
    public String getConfigIMEI() {
        if (mINIFile != null) {
            String imei = mINIFile.getStringProperty("CENTER", "imei");
            return imei;
        }
        
        return null;
    }
}
