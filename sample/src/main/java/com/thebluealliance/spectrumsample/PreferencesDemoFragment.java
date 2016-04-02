package com.thebluealliance.spectrumsample;

import com.thebluealliance.spectrum.SpectrumPreferenceCompat;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

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
