package com.thebluealliance.spectrumsample;

import com.thebluealliance.spectrum.SpectrumDialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
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

        findPreference("demo_dialog_3").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override public boolean onPreferenceClick(Preference preference) {
                showDialog3();
                return true;
            }
        });

        findPreference("demo_dialog_4").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override public boolean onPreferenceClick(Preference preference) {
                showDialog4();
                return true;
            }
        });

        findPreference("demo_dialog_5").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override public boolean onPreferenceClick(Preference preference) {
                showDialog5();
                return true;
            }
        });

        findPreference("demo_dialog_6").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDialog6();
                return true;
            }
        });
    }

    private void showDialog1() {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.demo_colors)
                .setSelectedColorRes(R.color.md_blue_500)
                .setDismissOnColorSelected(true)
                .setOutlineWidth(2)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
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
                .setOutlineWidth(2)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color).toUpperCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Dialog cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show(getFragmentManager(), "dialog_demo_2");
    }

    private void showDialog3() {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.many_shades_of_grey)
                .setSelectedColorRes(R.color.md_black)
                .setDismissOnColorSelected(false)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color).toUpperCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Dialog cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show(getFragmentManager(), "dialog_demo_3");
    }

    private void showDialog4() {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.demo_colors)
                .setSelectedColorRes(R.color.md_blue_500)
                .setDismissOnColorSelected(true)
                .setFixedColumnCount(4)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color).toUpperCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Dialog cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show(getFragmentManager(), "dialog_demo_4");
    }

    private void showDialog5() {
        new SpectrumDialog.Builder(getContext(), R.style.DialogTheme)
                .setColors(R.array.demo_colors)
                .setSelectedColorRes(R.color.md_blue_500)
                .setDismissOnColorSelected(true)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color).toUpperCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Dialog cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show(getFragmentManager(), "dialog_demo_5");
    }

    private void showDialog6() {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.demo_colors)
                .setSelectedColorRes(R.color.md_blue_500)
                .setDismissOnColorSelected(true)
                .setOutlineWidth(6)
                .setOutlineColor(Color.GREEN)
                .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color).toUpperCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Dialog cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show(getFragmentManager(), "dialog_demo_7");
    }

}
