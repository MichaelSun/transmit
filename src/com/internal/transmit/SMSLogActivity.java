package com.internal.transmit;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.internal.transmit.db.DatabaseOperator;

public class SMSLogActivity extends Activity {
    private static final String TAG = "InboxActivity";
    private static final boolean DEBUG = true;
    
    public static final String SMS_TYPE = "sms_type";
    
    private ListView mListView;
    private InfoAdapter mAdapter;
    private ArrayList<MessageInfo> mData;
    private boolean mIsCDMAType;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mIsCDMAType = getIntent().getBooleanExtra(SMS_TYPE, true);
        if (mIsCDMAType) {
            this.setTitle(getString(R.string.cdma));
        } else {
            this.setTitle(getString(R.string.gsm));
        }
        this.setContentView(R.layout.outbox);
        
        mListView = (ListView) findViewById(R.id.list);
        mData = DatabaseOperator.getInstance().getRecievedSMSLog(mIsCDMAType);
            
        if (DEBUG) {
            if (mData == null) {
                return;
            }
            for (MessageInfo info : mData) {
                Log.d(TAG, "[[onCreate]] info = " + info.toString());
            }
        }
        
        mAdapter = new InfoAdapter(this, R.layout.list_item, mData);
        mListView.setAdapter(mAdapter);
    }
    
    class InfoAdapter extends ArrayAdapter<MessageInfo> {
        private int mResourceID;
        private Context mContext;
        private LayoutInflater mInflater;
        
        InfoAdapter(Context context, int resourceId, ArrayList<MessageInfo> data) {
            super(context, resourceId, data);
            mResourceID = resourceId;
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        
        public View  getView(int position, View convertView, ViewGroup parent) {
            View ret = convertView;
            if (ret == null) {
                ret = mInflater.inflate(mResourceID, null);
            }
            
            MessageInfo info = this.getItem(position);
            TextView tv = (TextView) ret.findViewById(R.id.time);
            tv.setText(String.format(getString(R.string.time_format_inbox), info.time));
            tv = (TextView) ret.findViewById(R.id.content);
            tv.setText(String.format(getString(R.string.content), info.content));
            
            return ret;
        }
    }
}
