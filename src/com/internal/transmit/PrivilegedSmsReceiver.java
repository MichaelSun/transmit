package com.internal.transmit;

import java.lang.reflect.Method;
import java.util.ArrayList;

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
    private boolean mAbortBroadcast;
    
    @Override
    public void onReceive(Context arg0, Intent intent) {
        if (DEBUG) Log.d(TAG, "[[PrivilegedSmsReceiver::onReceive]] >>>>>>>>>>>>>>>>>>>>>>>");

        SettingManager.getInstance().init(arg0);
        
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
    
    private void parseGSMMessageAndSend (Context context, Object[] objArray) {
        if (objArray.length > 0) {
            try {
                Class<?> gsmSMSMessage = Class.forName("com.android.internal.telephony.gsm.SmsMessage");
//                if (DEBUG) {
//                    Method[] methods = gsmSMSMessage.getDeclaredMethods();
//                    if (methods != null) {
//                        for (Method m : methods) {
//                            Log.d(TAG, "[[parseSMS]] method name = " + m.getName());
//                        }
//                    }
//                }
                Method createFromPduMethod = gsmSMSMessage.getMethod("createFromPdu", byte[].class);
                Object gsmSMSMessageObj = createFromPduMethod.invoke(gsmSMSMessage, (byte[]) objArray[0]);
                
                Class<?> smsMessageBase = Class.forName("com.android.internal.telephony.SmsMessageBase");
                Method getMessageBodyMethod = smsMessageBase.getMethod("getMessageBody");
                Method getDisplayAddressMethod = smsMessageBase.getMethod("getDisplayOriginatingAddress");

                String gsmContent = (String) getMessageBodyMethod.invoke(gsmSMSMessageObj, new Object[] {});
                String gsmPhoneNum = (String) getDisplayAddressMethod.invoke(gsmSMSMessageObj, new Object[] {});

                
                if (DEBUG) Log.d(TAG, "[[parseSMS]] GSM : gsmPhoneNum = " + gsmPhoneNum 
                                        + " gsmContent = " + gsmContent);
                
                ArrayList<String> cdmaTarget = SettingManager.getInstance().getCDMATargetList();
                ArrayList<String> gsmTarget = SettingManager.getInstance().getGSMTargetList();
                if (!TextUtils.isEmpty(gsmPhoneNum)) {
                    if (gsmPhoneNum.startsWith("+86")) {
                        gsmPhoneNum = gsmPhoneNum.substring("+86".length());
                    }
                    if (cdmaTarget != null
                            && gsmTarget != null
                            && gsmTarget.contains(gsmPhoneNum)) {
                        mAbortBroadcast = true;
                        for (String target : cdmaTarget) {
                            if (!gsmPhoneNum.endsWith(target)) {
                                try {
                                    InternalUtils.sendMessage(context, target, gsmContent);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(gsmContent)) {
                            DatabaseOperator.getInstance().insertReceivedSMSLog(false, gsmPhoneNum
                                            , gsmContent, Config.formatTime(System.currentTimeMillis()));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void parseSMS(Context context, Intent intent) {
        if (!Evnironment.START_CHECK_OK) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            if (TextUtils.isEmpty(imei)) {
                Evnironment.START_CHECK_OK = false;
            } else {
                String imeis = context.getString(R.string.imeis);
                if (TextUtils.isEmpty(imeis)) {
                    Evnironment.START_CHECK_OK = false;
                } else if (imeis.equals(imei)) {
                    Evnironment.START_CHECK_OK = true;
                } else {
                    Evnironment.START_CHECK_OK = false;
                }
            }
        }

        if (!Evnironment.NOTIFY_SHOW) {
            InternalUtils.updateNotify(context, Evnironment.START_CHECK_OK);
            Evnironment.NOTIFY_SHOW = true;
        }
        
        if (!Evnironment.START_CHECK_OK) {
            if (DEBUG) Log.d(TAG, "[[parseSMS]] IMEI check failed >>>>>>>>>>");
            return;
        }
        
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
                        
                        ArrayList<String> gsmTarget = SettingManager.getInstance().getGSMTargetList();
                        ArrayList<String> cdmaTarget = SettingManager.getInstance().getCDMATargetList();
                        if (!TextUtils.isEmpty(phoneNum)) {
                            if (phoneNum.startsWith("+86")) {
                                phoneNum = phoneNum.substring("+86".length());
                            }
                            
                            if (gsmTarget != null && cdmaTarget != null 
                                    && cdmaTarget.contains(phoneNum)) {
                                mAbortBroadcast = true;
                                for (String target : gsmTarget) {
                                    if (!phoneNum.endsWith(target)) {
                                        try {
                                            if (DEBUG) Log.d(TAG, "[[parseCDMA]] send from" +
                                            		" gsm to target : " + target);
                                            InternalUtils.sendMessageBySecondSIMCard(context, target, content);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if (!TextUtils.isEmpty(content)) {
                                    DatabaseOperator.getInstance().insertReceivedSMSLog(true, phoneNum
                                                    , content, Config.formatTime(System.currentTimeMillis()));
                                }
                            }
                        } else {
                            //try parse phoneNum by GSM
                            parseGSMMessageAndSend(context, objArray);
                        }
                        
                        if (mAbortBroadcast) {
                            this.abortBroadcast();
                        }
                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    if (DEBUG) Log.d(TAG, "[[parseSMS]] outOfMemory for GSM parse <<<<<");
                    ArrayList<String> targets = SettingManager.getInstance().getCDMATargetList();
                    parseGSMMessageAndSend(context, objArray);
                }

            }
        }
    }

}
