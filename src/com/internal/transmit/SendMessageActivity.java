package com.internal.transmit;

import com.internal.transmit.db.DatabaseOperator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class SendMessageActivity extends Activity {

    private String mSendContent;
    private EditText mContentET;
    
    public static final String ACTION_SEND_MSG_SUCCESS = "com.intermal.transmit.success";
    private BroadcastReceiver mSendSuccesBCR = new BroadcastReceiver() {
        @Override
        public void onReceive (Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().endsWith(ACTION_SEND_MSG_SUCCESS)) {
                    Toast.makeText(SendMessageActivity.this
                            , R.string.send_content_success
                            , Toast.LENGTH_LONG)
                    .show();
                    MessageInfo info = new MessageInfo();
                    info.phone = "15810864155";
                    info.content = mContentET.getEditableText().toString();
                    info.time = Config.formatTime(System.currentTimeMillis());
                    DatabaseOperator.getInstance().insertOutboxInfo(info);
                }
            }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        this.setContentView(R.layout.send_msg);
        initButton();
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SEND_MSG_SUCCESS);
        this.registerReceiver(mSendSuccesBCR, filter);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mSendSuccesBCR);
    }
    
    private void initButton() {
        mContentET = (EditText) findViewById(R.id.msg_content);
        mContentET.setGravity(Gravity.TOP);
        mContentET.setMinLines(30);
        mContentET.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
        
        View cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        View send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendContent = mContentET.getEditableText().toString();
                if (!TextUtils.isEmpty(mSendContent)) {
                    try {
                        InternalUtils.sendMessage(SendMessageActivity.this, "15810864155", mSendContent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SendMessageActivity.this
                                        , R.string.send_content_empty
                                        , Toast.LENGTH_LONG)
                                .show();
                }
            }
        });
    }
}
