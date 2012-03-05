package com.internal.transmit.utils;

import java.util.ArrayList;

import com.internal.transmit.Evnironment;
import com.internal.transmit.R;
import com.internal.transmit.R.string;

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
    
//    public String getPhoneNum() {
//        String ret = mSharedPreferences.getString(mContext.getString(R.string.pref_phone_num), Evnironment.DEFAULT_TARGET);
//        if (!TextUtils.isEmpty(ret)) {
//            try {
//                ret = EncryptUtils.Decrypt(ret, EncryptUtils.SECRET_KEY);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        
//        return ret;
//    }
    
//    public void setPhoneNum(String phone) {
//        mEditor.putString(mContext.getString(R.string.pref_phone_num), phone);
//        mEditor.commit();
//    }
    
    public boolean getIsCenter() {
        return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_is_center), false);
    }
    
    public void setIsCenter(boolean isCenter) {
        mEditor.putBoolean(mContext.getString(R.string.pref_is_center), isCenter);
        mEditor.commit();
    }
    
    public ArrayList<String> getCDMATargetList() {
        String numlist = INIFileHelper.getInstance().getStringProperty(Config.SECTION_CDMA
                                        , Config.PROPERTY_NUMLIST, ";");
        
        if (!TextUtils.isEmpty(numlist)) {
            ArrayList<String> ret = new ArrayList<String>();
            String[] targetArray = numlist.split(";");
            for (String target : targetArray) {
                if (!TextUtils.isEmpty(target)) {
                    ret.add(target);
                }
            }
            return ret;
        }
        
        return null;
    }
    
    public void setCDMATargetList(ArrayList<String> targetList) {
        if (targetList != null) {
            StringBuffer saved = new StringBuffer();
            for (String target : targetList) {
                saved.append(target).append(";");
            }
            String targetNumList = saved.substring(0, saved.length() - 1);
            
            INIFile inifile = new INIFile(Config.CONFIG_FILE_PATH);
            inifile.setFileName(Config.CONFIG_FILE_PATH);
            
            try {
                inifile.setStringProperty("CDMA", "numlist"
                        , EncryptUtils.Encrypt(targetNumList, EncryptUtils.SECRET_KEY), null);
                inifile.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public ArrayList<String> getGSMTargetList() {
        String numlist = INIFileHelper.getInstance().getStringProperty(Config.SECTION_GSM
                                        , Config.PROPERTY_NUMLIST, ";");

        if (!TextUtils.isEmpty(numlist)) {
            ArrayList<String> ret = new ArrayList<String>();
            String[] targetArray = numlist.split(";");
            for (String target : targetArray) {
                if (!TextUtils.isEmpty(target)) {
                    ret.add(target);
                }
            }
            return ret;
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
        
        if (targetList != null) {
            StringBuffer saved = new StringBuffer();
            for (String target : targetList) {
                saved.append(target).append(";");
            }
            String targetNumList = saved.substring(0, saved.length() - 1);
            
            INIFile inifile = new INIFile(Config.CONFIG_FILE_PATH);
            inifile.setFileName(Config.CONFIG_FILE_PATH);
            
            try {
                inifile.setStringProperty("GSM", "numlist"
                        , EncryptUtils.Encrypt(targetNumList, EncryptUtils.SECRET_KEY), null);
                inifile.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public String getTargetNumber() {
        return mSharedPreferences.getString(mContext.getString(R.string.target_number), null);
    }
    
    public void setTargetNumber(String number) {
        mEditor.putString(mContext.getString(R.string.target_number), number);
        mEditor.commit();
    }
    
}
