package com.internal.transmit;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TargetSettingActivity extends Activity {

    private ListView mList;
    private TargetInfoAdapter mAdapter;
    private ArrayList<String> mTargetList;
    private EditText mEditText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setTitle(R.string.title_convert);
        
        this.setContentView(R.layout.outbox);
        mList = (ListView) findViewById(R.id.list);
        mTargetList = SettingManager.getInstance().getTargetList();
        if (mTargetList != null) {
            mAdapter = new TargetInfoAdapter(this, R.layout.target_list_item, mTargetList);
            mList.setAdapter(mAdapter);
        }
        
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position < mTargetList.size()) {
                    AlertDialog dialog = new AlertDialog.Builder(TargetSettingActivity.this)
                                                .setMessage(R.string.delete_tips)
                                                .setPositiveButton(R.string.btn_ok, 
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                mTargetList.remove(position);
                                                                SettingManager.getInstance().setTargetList(mTargetList);
                                                                if (mTargetList != null) {
                                                                    mAdapter = new TargetInfoAdapter(TargetSettingActivity.this, R.layout.target_list_item, mTargetList);
                                                                    mList.setAdapter(mAdapter);
                                                                }
                                                            }
                                                        })
                                                .setNegativeButton(R.string.btn_cancel, null)
                                                .create();
                    dialog.show();
                }
                
                return true;
            }
        });
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
                                                        if (!mTargetList.contains(phone)) {
                                                            mTargetList.add(phone);
                                                            SettingManager.getInstance().setTargetList(mTargetList);
                                                            if (mTargetList != null) {
                                                                mAdapter = new TargetInfoAdapter(TargetSettingActivity.this, R.layout.target_list_item, mTargetList);
                                                                mList.setAdapter(mAdapter);
                                                            }
                                                        }
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
