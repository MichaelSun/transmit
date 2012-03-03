package com.internal.transmit.sendclinet;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(OutboxActivity.this)
                                            .setPositiveButton(R.string.btn_delete
                                                    , new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (mData != null && mData.size() > 0 && position < mData.size()) {
                                                            MessageInfo info = mData.get(position);
                                                            DatabaseOperator.getInstance().deleteOutboxInfo(info);
                                                            mData = DatabaseOperator.getInstance().queryOutbox();
                                                            mAdapter = new InfoAdapter(OutboxActivity.this, R.layout.list_item, mData);
                                                            mListView.setAdapter(mAdapter);
                                                        }
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

        public View getView(int position, View convertView, ViewGroup parent) {
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
