package com.internal.transmit;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

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
}
