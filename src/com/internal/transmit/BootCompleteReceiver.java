package com.internal.transmit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        if (TextUtils.isEmpty(imei)) {
            InternalUtils.updateNotify(context, false);
            Evnironment.START_CHECK_OK = false;
        } else {
            String imeis = context.getString(R.string.imeis);
            if (TextUtils.isEmpty(imeis)) {
                InternalUtils.updateNotify(context, false);
                Evnironment.START_CHECK_OK = false;
            } else if (imeis.equals(imei)) {
                InternalUtils.updateNotify(context, true);
                Evnironment.START_CHECK_OK = true;
            } else {
                InternalUtils.updateNotify(context, false);
                Evnironment.START_CHECK_OK = false;
            }
        }
        
        Evnironment.NOTIFY_SHOW = true;
        
    }

}
