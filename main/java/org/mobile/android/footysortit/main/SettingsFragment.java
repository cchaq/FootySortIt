package org.mobile.android.footysortit.main;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.mobile.android.footysortit.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_settings);

    }

    protected void loadPrivacyPage(){

    }
}
