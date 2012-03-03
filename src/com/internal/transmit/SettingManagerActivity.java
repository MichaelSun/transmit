package com.internal.transmit;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingManagerActivity extends PreferenceActivity {

    private EditTextPreference mNumPreference;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.addPreferencesFromResource(R.xml.setting);
        
//        mNumPreference = (EditTextPreference) findPreference(getResources().getString(R.string.pref_phone_num));
//        mNumPreference.setSummary(SettingManager.getInstance().getPhoneNum());
//        
//        mNumPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            public boolean  onPreferenceChange(Preference preference, Object newValue) {
//                mNumPreference.setSummary(newValue.toString());
//                return true;
//            }
//        });
    }
}
