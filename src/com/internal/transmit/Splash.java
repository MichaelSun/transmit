package com.internal.transmit;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.internal.transmit.db.DatabaseOperator;
import com.internal.transmit.sendclinet.LoginActivity;
import com.internal.transmit.utils.Config;
import com.internal.transmit.utils.INIFileHelper;
import com.internal.transmit.utils.InternalUtils;
import com.internal.transmit.utils.SettingManager;

public class Splash extends Activity {
    private static final String TAG = "Splash";
    
    private StartTask mStartTask;
    
    private static final int SPLASH_END = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SPLASH_END:
                if (!Evnironment.checkIMEIAndIMSI(Splash.this
                                , SettingManager.getInstance().getIsCenter())) {
                    Toast.makeText(Splash.this, 
                            Splash.this.getString(R.string.confirm_error),
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    if (!SettingManager.getInstance().getIsCenter()) {
                        Intent mainIntent = new Intent();
                        mainIntent.setClass(getApplicationContext(), LoginActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        if (!Evnironment.NOTIFY_SHOW) {
                            InternalUtils.updateNotify(Splash.this, Evnironment.START_CHECK_OK, null);                    
                            Evnironment.NOTIFY_SHOW = true;
                        }
                        
                        Intent target = new Intent();
                        target.setClass(getApplicationContext(), TargetSettingActivity.class);
                        startActivity(target);
                        finish();
                    }
                }
                break;
            }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.start_activity);
        
        INIFileHelper.getInstance().init(Config.CONFIG_FILE_PATH);
        
        DatabaseOperator.getInstance().init(Splash.this.getApplicationContext());
        SettingManager.getInstance().init(getApplicationContext());
        
        TextView content = (TextView) findViewById(R.id.content);
        boolean center = INIFileHelper.getInstance()
                            .getBooleanProperty(Config.SECTION_CENTER, Config.PROPERTY_CENTER);
        SettingManager.getInstance().setIsCenter(center);
        if (!center) {
            String target = INIFileHelper.getInstance()
                                .getStringProperty(Config.SECTION_CENTER, Config.PROPERTY_TARGET);
            SettingManager.getInstance().setTargetNumber(target);
        }
        
        LOGD("[[onCreate]] center = " + center + " target = " 
                    + SettingManager.getInstance().getTargetNumber() + " >>>>>>>>");
        
        if (SettingManager.getInstance().getIsCenter()) {
            content.setText(R.string.center_content);
        } else {
            content.setText(R.string.main_content);
        }
        
        mStartTask = new StartTask();
        mStartTask.execute("");
    }
    
    private class StartTask extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String...params) {
            try {
                if (Config.DEBUG) {
                    LOGD("[[StartTask]] IMSI = " + Evnironment.getIMSI(Splash.this) + " >>>>>>>");
                }
                Thread.sleep(2 * 1000);
            } catch (Exception e) {
            }
            
            return 0;
        }
        
        protected void onPostExecute(Integer result) {
            mHandler.sendEmptyMessage(SPLASH_END);
        }
    }
    
    private static void LOGD(String msg) {
        if (Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
