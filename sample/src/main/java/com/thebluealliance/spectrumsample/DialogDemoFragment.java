package com.thebluealliance.spectrumsample;

import com.thebluealliance.spectrum.SpectrumDialog;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Even though we aren't demoing preferences here, we use a list of preferences as the UI to launch
 * the dialogs because it's easy.
 */
public class DialogDemoFragment extends PreferenceFragmentCompat {

    @Override public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.demo_dialog);

        findPreference("demo_dialog_1").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override public boolean onPreferenceClick(Preference preference) {
                showDialog1();
                return true;
            }
        });

        findPreference("demo_dialog_2").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override public boolean onPreferenceClick(Preference preference) {
                showDialog2();
                return true;
            }
        });
    }

    private void showDialog1() {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.demo_colors)
                .setSelectedColorRes(R.color.md_blue_500)
                .setDismissOnColorSelected(true)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if(positiveResult) {
                            Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color).toUpperCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Dialog cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show(getFragmentManager(), "dialog_demo_1");
    }

    private void showDialog2() {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.demo_colors)
                .setSelectedColorRes(R.color.md_blue_500)
                .setDismissOnColorSelected(false)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if(positiveResult) {
                            Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color).toUpperCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Dialog cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show(getFragmentManager(), "dialog_demo_2");
    }
}
