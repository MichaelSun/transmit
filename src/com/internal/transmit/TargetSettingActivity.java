package com.internal.transmit;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TargetSettingActivity extends Activity {

    private ListView mList;
    private TargetInfoAdapter mAdapter;
    private EditText mEditText;
    private Button mGSMListButton;
    private Button mCDMAListButton;
    private ArrayList<String> mGSMList;
    private ArrayList<String> mCDMAList;
    private boolean mShowCMDA;
    
    private static final int REFRESH_LIST = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case REFRESH_LIST:
                if (mShowCMDA) {
                    setTitle(String.format(getString(R.string.title_convert), getString(R.string.cdma)));
                    if (mCDMAList != null) {
                        mAdapter = new TargetInfoAdapter(TargetSettingActivity.this, R.layout.target_list_item, mCDMAList);
                        mList.setAdapter(mAdapter);
                    }
                } else {
                    setTitle(String.format(getString(R.string.title_convert), getString(R.string.gsm)));
                    if (mGSMList != null) {
                        mAdapter = new TargetInfoAdapter(TargetSettingActivity.this, R.layout.target_list_item, mGSMList);
                        mList.setAdapter(mAdapter);
                    }
                }
                break;
            }
        }
    };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle(String.format(getString(R.string.title_convert), getString(R.string.cdma)));
        this.setContentView(R.layout.outbox);
        View buttonRegion = findViewById(R.id.title_region);
        buttonRegion.setVisibility(View.VISIBLE);
        mCDMAListButton = (Button) buttonRegion.findViewById(R.id.button1);
        mGSMListButton = (Button) buttonRegion.findViewById(R.id.button2);
        mGSMList = SettingManager.getInstance().getGSMTargetList();
        mCDMAList = SettingManager.getInstance().getCDMATargetList();
        mShowCMDA = true;
        
        mCDMAListButton.setText("CDMA");
        mCDMAListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowCMDA = true;
                mHandler.sendEmptyMessage(REFRESH_LIST);
            }
        });
        mGSMListButton.setText("GSM");
        mGSMListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowCMDA = false;
                mHandler.sendEmptyMessage(REFRESH_LIST);
            }
        });
        
        mList = (ListView) findViewById(R.id.list);
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                ArrayList<String> targetList = mShowCMDA ? mCDMAList : mGSMList;
                
                if (position < targetList.size()) {
                    AlertDialog dialog = new AlertDialog.Builder(TargetSettingActivity.this)
                                                .setMessage(R.string.delete_tips)
                                                .setPositiveButton(R.string.btn_ok, 
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if (mShowCMDA) {
                                                                    mCDMAList.remove(position);
                                                                    SettingManager.getInstance().setCDMATargetList(mCDMAList);
                                                                } else {
                                                                    mGSMList.remove(position);
                                                                    SettingManager.getInstance().setGSMTargetList(mGSMList);
                                                                }
                                                                mHandler.sendEmptyMessage(REFRESH_LIST);
                                                            }
                                                        })
                                                .setNegativeButton(R.string.btn_cancel, null)
                                                .create();
                    dialog.show();
                }
                
                return true;
            }
        });
        
        mHandler.sendEmptyMessage(REFRESH_LIST);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.target_menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.add).setIcon(android.R.drawable.ic_menu_add);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add:
            showAddTargetDialog();
            break;
        case R.id.cdma_log:
            {
                Intent log = new Intent();
                log.setClass(getApplicationContext(), SMSLogActivity.class);
                log.putExtra(SMSLogActivity.SMS_TYPE, true);
                startActivity(log);
            }
            break;
        case R.id.gsm_log:
            {
                Intent log = new Intent();
                log.setClass(getApplicationContext(), SMSLogActivity.class);
                log.putExtra(SMSLogActivity.SMS_TYPE, false);
                startActivity(log);
            }
            break;
        }
        
        return true;
    }
    
    private void showAddTargetDialog() {
        mEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                                    .setView(mEditText)
                                    .setTitle(R.string.add_target)
                                    .setPositiveButton(R.string.btn_ok, 
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String phone = mEditText.getEditableText().toString();
                                                    if (!TextUtils.isEmpty(phone)
                                                            && phone.length() == 11) {
                                                        if (mShowCMDA) {
                                                            if (!mCDMAList.contains(phone)) {
                                                                mCDMAList.add(phone);
                                                                SettingManager.getInstance().setCDMATargetList(mCDMAList);
                                                            }
                                                        } else {
                                                            if (!mGSMList.contains(phone)) {
                                                                mGSMList.add(phone);
                                                                SettingManager.getInstance().setGSMTargetList(mGSMList);
                                                            }
                                                        }
                                                        mHandler.sendEmptyMessage(REFRESH_LIST);
                                                    } else {
                                                        Toast.makeText(TargetSettingActivity.this, R.string.add_error_tips, Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            })
                                    .setNegativeButton(R.string.btn_cancel, null)
                                    .create();
        dialog.show();
    }
    
    class TargetInfoAdapter extends ArrayAdapter<String> {
        private int mResourceID;
        private Context mContext;
        private LayoutInflater mInflater;

        TargetInfoAdapter(Context context, int resourceId, ArrayList<String> data) {
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

            String info = this.getItem(position);
            TextView tv = (TextView) ret.findViewById(R.id.text);
            tv.setText(info);

            return ret;
        }
    }
}
