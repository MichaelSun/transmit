package com.internal.transmit;

import com.internal.transmit.utils.Config;
import com.internal.transmit.utils.INIFileHelper;
import com.internal.transmit.utils.InternalUtils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class Evnironment {

    public static boolean NOTIFY_SHOW = false;
    
    public static boolean START_CHECK_OK = false;
    
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        
        return telephonyManager.getDeviceId();
    }
    
    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        
        return imsi;
    }
    
    public static boolean checkIMEIAndIMSI(Context context, boolean isCenter) {
        String imei = Evnironment.getIMEI(context);
        String imsi = Evnironment.getIMSI(context);
        if (TextUtils.isEmpty(imei) || TextUtils.isEmpty(imsi)) {
            if (isCenter) {
                InternalUtils.updateNotify(context, false, null);
            }
            Evnironment.START_CHECK_OK = false;
        } else {
            String imeis = INIFileHelper.getInstance().getStringProperty(
                                            Config.SECTION_CENTER
                                            , Config.PROPERTY_IMEI);
            String imsis = INIFileHelper.getInstance().getStringProperty(
                                            Config.SECTION_CENTER
                                            , Config.PROPERTY_IMSI);
            if (TextUtils.isEmpty(imeis) || TextUtils.isEmpty(imsis)) {
                if (isCenter) {
                    InternalUtils.updateNotify(context, false, null);
                }
                Evnironment.START_CHECK_OK = false;
            } else if (imeis.equals(imei) && imsis.equals(imsi)) {
                if (isCenter) {
                    InternalUtils.updateNotify(context, true, null);
                }
                Evnironment.START_CHECK_OK = true;
            } else {
                if (isCenter) {
                    InternalUtils.updateNotify(context, false, null);
                }
                Evnironment.START_CHECK_OK = false;
            }
        }
        
        return Evnironment.START_CHECK_OK;
    }
}
