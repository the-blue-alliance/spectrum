package com.thebluealliance.spectrumsample;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;

import com.thebluealliance.spectrum.SpectrumPreferenceCompat;

import java.util.Random;

public class ExternalColorChangePreference extends Preference {

    private final String mSpectrumPreferenceId;

    public ExternalColorChangePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .ExternalColorChangePreference, 0, 0);
        try {
            mSpectrumPreferenceId = a.getString(R.styleable.ExternalColorChangePreference_spectrum_preference);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        final SpectrumPreferenceCompat colorPreference = (SpectrumPreferenceCompat) getPreferenceManager().findPreference(mSpectrumPreferenceId);
        final int[] colors = colorPreference.getColors();
        final NonRepeatingRandom randomGenerator = new NonRepeatingRandom();
        setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                colorPreference.setColor(colors[randomGenerator.nextInt(colors.length)]);
                return false;
            }
        });
    }

    private class NonRepeatingRandom extends Random {

        private int mLastRandomInt = -1;

        @Override
        public int nextInt(int n) {
            int random = super.nextInt(n);
            return mLastRandomInt = (random == mLastRandomInt ? nextInt(n) : random);
        }
    }
}
