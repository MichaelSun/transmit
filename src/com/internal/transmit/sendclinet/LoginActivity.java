package com.internal.transmit.sendclinet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.internal.transmit.R;
import com.internal.transmit.utils.Config;
import com.internal.transmit.utils.INIFileHelper;

public class LoginActivity extends Activity {

    private int mPasswordErrorCount;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        this.setContentView(R.layout.login_dialog);
        
        View login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = ((EditText) findViewById(R.id.password)).getEditableText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, R.string.empty_password, Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                
                String internal_password = INIFileHelper.getInstance()
                                            .getStringProperty(Config.SECTION_CENTER
                                                        , Config.PROPERTY_ENTER_PASSWORD);
                if (TextUtils.isEmpty(internal_password)) {
                    internal_password = getString(R.string.internal_password);
                }
                if (password.equals(internal_password)) {
                    Intent mainIntent = new Intent();
                    mainIntent.setClass(getApplicationContext(), MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    if (mPasswordErrorCount >= 3) {
                        Toast.makeText(LoginActivity.this, R.string.password_error_too_many
                                        , Toast.LENGTH_LONG)
                                .show();
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.error_password, Toast.LENGTH_LONG)
                            .show();
                        mPasswordErrorCount++;
                    }
                }
            }
        });
    }
}
