package com.thebluealliance.spectrumsample;

import android.os.Bundle;


import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.thebluealliance.spectrum.SpectrumPreferenceCompat;

public class PreferencesDemoFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        try {
            addPreferencesFromResource(R.xml.demo_preferences);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause().printStackTrace();
        }
    }

    @Override public void onDisplayPreferenceDialog(Preference preference) {
        if (!SpectrumPreferenceCompat.onDisplayPreferenceDialog(preference, this)) {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
