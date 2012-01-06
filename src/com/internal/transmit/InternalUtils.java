package com.internal.transmit;

import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class InternalUtils {
    
    private static final int NOTIFY_ID = 1;
    public static void updateNotify(Context context, boolean success) {
        Intent accountIntent = new Intent(context, TargetSettingActivity.class);
        PendingIntent intent = PendingIntent.getActivity(context, 0, accountIntent, 0);
        Notification notif1 = null;
        if (success) {
            notif1 = new Notification(R.drawable.icon_success, 
                                    context.getString(R.string.tips_success),
                                    System.currentTimeMillis());
        } else {
            notif1 = new Notification(R.drawable.icon_failed, 
                                    context.getString(R.string.tips_failed),
                                    System.currentTimeMillis());
        }
        notif1.vibrate = new long[] { 100, 250, 100, 500 };
        notif1.setLatestEventInfo(context, context.getString(R.string.tips_title),
                                    (success 
                                         ? context.getString(R.string.tips_success)
                                         : context.getString(R.string.tips_failed)), 
                                    intent);
        notif1.flags |= Notification.FLAG_NO_CLEAR;
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFY_ID, notif1);
    }

    public static void sendMessage(Context context, String target, String content) throws Exception {
        if (content == null) {
            throw new Exception("Null message body or have multiple destinations.");
        }
        
        SmsManager smsManager = SmsManager.getDefault();
        target = target.replaceAll(" ", "");

        Intent send = new Intent();
        send.setAction(SendMessageActivity.ACTION_SEND_MSG_SUCCESS);
        PendingIntent pt = PendingIntent.getBroadcast(context, 0, send, 0);
        try {
            smsManager.sendTextMessage(target, null, content, pt, null);
        } catch (Exception ex) {
            throw new Exception("SmsMessageSender.sendMessage: caught " + ex +
                    " from SmsManager.sendTextMessage()");
        }
        
        return;
    }
    
    public static void sendMessageBySecondSIMCard(Context context, String target, String content) throws Exception {
        if (content == null) {
            throw new Exception("Null message body or have multiple destinations.");
        }
        
        Class<?> SecondarySmsManager;
        SecondarySmsManager = Class.forName("com.motorola.android.telephony.SecondarySmsManager");
        Method getDefault = SecondarySmsManager.getMethod("getDefault");
        Object SecondarySmsManagerObject = getDefault.invoke(SecondarySmsManager);
        
        Method[] methods = SecondarySmsManager.getClass().getMethods();
        for (Method m : methods) {
            Log.d("sendMessageBySecondSIMCard", "Method name = " + m.getName());
        }
        
        Method sendTextMessage = SecondarySmsManager.getMethod("sendTextMessage", String.class, String.class,
                String.class, PendingIntent.class, PendingIntent.class);
        
        target = target.replaceAll(" ", "");

        Intent send = new Intent();
        send.setAction(SendMessageActivity.ACTION_SEND_MSG_SUCCESS);
        PendingIntent pt = PendingIntent.getBroadcast(context, 0, send, 0);
        try {
            sendTextMessage.invoke(SecondarySmsManagerObject, target, null, content, pt, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return;
    }
}
