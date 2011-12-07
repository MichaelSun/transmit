package com.internal.transmit;

import com.internal.transmit.db.DatabaseOperator;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

public class Splash extends Activity {
    
    private StartTask mStartTask;
    
    private static final int SPLASH_END = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SPLASH_END:
                Intent mainIntent = new Intent();
                mainIntent.setClass(getApplicationContext(), LoginActivity.class);
                startActivity(mainIntent);
                finish();
                break;
            }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.start_activity);
        
        mStartTask = new StartTask();
        mStartTask.execute("");
    }
    
    private class StartTask extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String...params) {
            try {
                DatabaseOperator.getInstance().init(Splash.this.getApplicationContext());
                Thread.sleep(3 * 1000);
            } catch (Exception e) {
            }
            
            return 0;
        }
        
        protected void onPostExecute(Integer result) {
            mHandler.sendEmptyMessage(SPLASH_END);
        }
    }
}
