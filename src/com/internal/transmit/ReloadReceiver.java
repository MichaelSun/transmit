package com.internal.transmit;

import java.io.File;

import com.internal.transmit.utils.Config;
import com.internal.transmit.utils.INIFileHelper;
import com.internal.transmit.utils.InternalUtils;
import com.internal.transmit.utils.SettingManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SettingManager.getInstance().init(context);
        
        String action = intent.getAction();
        if (action.equals(Config.BROADCAST_ACTION)) {
            File file = new File(Config.CONFIG_FILE_PATH);
            if (!file.exists() || !file.isFile()) {
                InternalUtils.updateNofityForNoConfig(context);
                return;
            }
            INIFileHelper.getInstance().init(Config.CONFIG_FILE_PATH);
            
            boolean center = INIFileHelper.getInstance()
                                .getBooleanProperty(Config.SECTION_CENTER
                                        , Config.PROPERTY_CENTER);
            SettingManager.getInstance().setIsCenter(center);
            if (!SettingManager.getInstance().getIsCenter()) {
                String target = INIFileHelper.getInstance()
                                .getStringProperty(Config.SECTION_CENTER
                                        , Config.PROPERTY_TARGET);
                SettingManager.getInstance().setTargetNumber(target);
            }
            
            Evnironment.checkIMEIAndIMSI(context, SettingManager.getInstance().getIsCenter());
            Evnironment.NOTIFY_SHOW = true;
        }
    }

}
