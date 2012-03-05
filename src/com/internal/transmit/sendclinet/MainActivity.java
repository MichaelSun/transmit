package com.internal.transmit.sendclinet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.internal.transmit.R;
import com.internal.transmit.utils.Config;
import com.internal.transmit.utils.INIFileHelper;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        this.setContentView(R.layout.main);
        
        initButton();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.layout.map_menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.setting).setIcon(R.drawable.menu_setting);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//        case R.id.setting:
//            Intent intent = new Intent();
//            intent.setClass(this, SettingManagerActivity.class);
//            startActivity(intent);
//            break;
//        }
        
        return true;
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
        boolean can_send = INIFileHelper.getInstance().getBooleanProperty(Config.SECTION_CENTER, Config.PROPERTY_SEND);
        send.setEnabled(can_send);
        
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
