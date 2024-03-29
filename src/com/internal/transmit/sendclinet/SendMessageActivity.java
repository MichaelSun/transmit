package com.internal.transmit.sendclinet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.internal.transmit.MessageInfo;
import com.internal.transmit.R;
import com.internal.transmit.db.DatabaseOperator;
import com.internal.transmit.utils.Config;
import com.internal.transmit.utils.EncryptUtils;
import com.internal.transmit.utils.InternalUtils;
import com.internal.transmit.utils.SettingManager;

public class SendMessageActivity extends Activity {
    private static final String TAG = "SendMessageActivity";

    public static final String RELAY_SMS = "reply_sms";
    
    private static final int TEXT_COUNT = 20;
    
    private String mSendContent;
    private EditText mContentET;
    private TextView mCountTV;
    
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
                    info.phone = SettingManager.getInstance().getTargetNumber();
                    info.content = mContentET.getEditableText().toString();
                    info.time = Config.formatTime(System.currentTimeMillis());
                    DatabaseOperator.getInstance().insertOutboxInfo(info);
                    
                    finish();
                }
            }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        this.setContentView(R.layout.send_msg);
        boolean isReply = getIntent().getBooleanExtra(RELAY_SMS, false);
        if (isReply) {
            TextView titleTV = (TextView) findViewById(R.id.send_msg);
            titleTV.setText(R.string.reply_msg);
        }
        
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
        mCountTV = (TextView) findViewById(R.id.count);
        
        mContentET.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if(length > TEXT_COUNT) {
                    mCountTV.setTextColor(Color.RED);
                } else {
                    mCountTV.setTextColor(Color.WHITE);
                }
                mCountTV.setText(length + "/" + TEXT_COUNT);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            
        });
        
        View cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String content = mContentET.getEditableText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        AlertDialog dialog = new AlertDialog.Builder(SendMessageActivity.this)
                                                    .setMessage(getString(R.string.back_tips))
                                                    .setPositiveButton(R.string.btn_ok
                                                            , new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    finish();
                                                                }
                                                            })
                                                    .setNegativeButton(R.string.btn_cancel
                                                            , null)
                                                    .create();
                        
                        dialog.show();
                    } else {
                        finish();
                    }
//                    try {
//                        InternalUtils.sendMessageBySecondSIMCard(SendMessageActivity.this
//                                        , SettingManager.getInstance().getPhoneNum() 
//                                        , content);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
            }
        });
        
//        boolean can_send = INIFileHelper.getInstance().getBooleanProperty(Config.SECTION_CENTER
//                                    , Config.PROPERTY_SEND);
        View send = findViewById(R.id.send);
//        send.setEnabled(can_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendContent = mContentET.getEditableText().toString();
                if (!TextUtils.isEmpty(mSendContent)) {
                    if (mSendContent.length() > TEXT_COUNT) {
                        Toast.makeText(SendMessageActivity.this
                                , R.string.send_content_too_long
                                , Toast.LENGTH_LONG)
                        .show();
                        return;
                    }
                    try {
                        byte[] data = mSendContent.getBytes();
                        String sendData = EncryptUtils.byte2hex(data).toLowerCase();
                        LOGD("[[send::onClick]] sendData = " + sendData + " >>>>>>>>");
                        InternalUtils.sendMessage(SendMessageActivity.this
                                        , SettingManager.getInstance().getTargetNumber()
                                        , sendData);
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
    
    private static void LOGD(String msg) {
        if (Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
