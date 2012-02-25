package com.internal.transmit;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        
        File file = new File(Config.CONFIG_FILE_PATH);
        if (!file.exists()) {
            InternalUtils.updateNotify(context, false, context.getString(R.string.config_not_find));
            return;
        }
        
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        if (TextUtils.isEmpty(imei)) {
            InternalUtils.updateNotify(context, false, null);
            Evnironment.START_CHECK_OK = false;
        } else {
//            String imeis = context.getString(R.string.imeis);
            String imeis = SettingManager.getInstance().getConfigIMEI();
            if (TextUtils.isEmpty(imeis)) {
                InternalUtils.updateNotify(context, false, null);
                Evnironment.START_CHECK_OK = false;
            } else if (imeis.equals(imei)) {
                InternalUtils.updateNotify(context, true, null);
                Evnironment.START_CHECK_OK = true;
            } else {
                InternalUtils.updateNotify(context, false, null);
                Evnironment.START_CHECK_OK = false;
            }
        }
        
        Evnironment.NOTIFY_SHOW = true;
        
    }

}
