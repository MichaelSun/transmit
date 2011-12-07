package com.internal.transmit;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.internal.transmit.db.DatabaseOperator;

public class OutboxActivity extends Activity {
    private static final String TAG = "OutboxActivity";
    private static final boolean DEBUG = true;
    
    private ListView mListView;
    private InfoAdapter mAdapter;
    private ArrayList<MessageInfo> mData;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setTitle(getString(R.string.outbox));
        this.setContentView(R.layout.outbox);
        
        mListView = (ListView) findViewById(R.id.list);
        mData = DatabaseOperator.getInstance().queryOutbox();
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
            tv.setText(String.format(getString(R.string.time_format), info.time));
            tv = (TextView) ret.findViewById(R.id.content);
            tv.setText(String.format(getString(R.string.content), info.content));
            
            return ret;
        }
    }
}
