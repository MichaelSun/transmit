package com.internal.transmit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        
        InternalUtils.updateNotify(context, true);
        Evnironment.NOTIFY_SHOW = true;
        
    }

}
