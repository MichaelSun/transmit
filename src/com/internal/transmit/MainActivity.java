package com.internal.transmit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        this.setContentView(R.layout.main);
        
        initButton();
    }
    
    private void initButton() {
        View send = findViewById(R.id.send_msg);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent send_intent = new Intent();
                send_intent.setClass(getApplicationContext(), SendMessageActivity.class);
                startActivity(send_intent);
            }
        });
        
        View outbox = findViewById(R.id.outbox);
        outbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent send_intent = new Intent();
                send_intent.setClass(getApplicationContext(), OutboxActivity.class);
                startActivity(send_intent);
            }
        });
        
        View inbox = findViewById(R.id.inbox);
        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent send_intent = new Intent();
                send_intent.setClass(getApplicationContext(), InboxActivity.class);
                startActivity(send_intent);
            }
        });
    }
}
