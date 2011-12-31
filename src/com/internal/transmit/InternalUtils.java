package com.internal.transmit;

import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class InternalUtils {

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
