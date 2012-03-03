package com.internal.transmit.sendclinet;

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

import com.internal.transmit.MessageInfo;
import com.internal.transmit.R;
import com.internal.transmit.R.id;
import com.internal.transmit.R.layout;
import com.internal.transmit.R.string;
import com.internal.transmit.db.DatabaseOperator;
import com.internal.transmit.utils.SettingManager;

public class InboxActivity extends Activity {
    private static final String TAG = "InboxActivity";
    private static final boolean DEBUG = true;
    
    private ListView mListView;
    private InfoAdapter mAdapter;
    private ArrayList<MessageInfo> mData;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setTitle(getString(R.string.inbox));
        this.setContentView(R.layout.outbox);
        SettingManager.getInstance().init(this);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
        
        mListView = (ListView) findViewById(R.id.list);
        mData = DatabaseOperator.getInstance().queryInbox();
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
        
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(InboxActivity.this)
                                            .setPositiveButton(R.string.btn_delete
                                                    , new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (mData != null && mData.size() > 0 && position < mData.size()) {
                                                            MessageInfo info = mData.get(position);
                                                            DatabaseOperator.getInstance().deleteInboxInfo(info);
                                                            mData = DatabaseOperator.getInstance().queryInbox();
                                                            mAdapter = new InfoAdapter(InboxActivity.this, R.layout.list_item, mData);
                                                            mListView.setAdapter(mAdapter);
                                                        }
                                                    }
                                            })
                                            .setNegativeButton(R.string.btn_reply
                                                    , new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent reply = new Intent();
                                                            reply.setClass(getApplicationContext(), SendMessageActivity.class);
                                                            reply.putExtra(SendMessageActivity.RELAY_SMS, true);
                                                            startActivity(reply);
                                                        }
                                                    })
                                            .create();
                dialog.show();
                
                return true;
            }
        });
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
