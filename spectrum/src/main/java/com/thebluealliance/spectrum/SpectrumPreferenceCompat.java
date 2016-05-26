package com.thebluealliance.spectrum;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;

import com.thebluealliance.spectrum.internal.ColorCircleDrawable;
import com.thebluealliance.spectrum.internal.SpectrumPreferenceDialogFragmentCompat;

/**
 * A version of {@link SpectrumPreference} meant to be used with the support library Preferences.
 *
 * This preference should be hosted and displayed by a {@link PreferenceFragmentCompat} fragment.
 * Because the support library preferences work differently than normal preferences, you have to
 * subclass {@link PreferenceFragmentCompat} and override
 * {@link PreferenceFragmentCompat#onDisplayPreferenceDialog}. This method is tasked with creating
 * the appropriate dialog for a particular preference. This class provides a helper method,
 * {@link #onDisplayPreferenceDialog(Preference, PreferenceFragmentCompat)}, that makes this easy.
 * If that method returns true, the preference was an instance of {@link SpectrumPreferenceCompat}
 * and the dialog was displayed. If it returns false, you should call through to the super method
 * to ensure proper behavior with other preference types. An example of this is shown below.
 *
 * <pre>{@code
 * public void onDisplayPreferenceDialog(Preference preference) {
 *     if (!SpectrumPreferenceCompat.onDisplayPreferenceDialog(preference, this)) {
 *         super.onDisplayPreferenceDialog(preference);
 *     }
 * }
 * }
 * </pre>
 */
public class SpectrumPreferenceCompat extends DialogPreference {

    private static final String DIALOG_FRAGMENT_TAG = "android.support.v7.preference.PreferenceFragment.DIALOG";

    private static final @ColorInt int DEFAULT_VALUE = Color.BLACK;
    public static final int ALPHA_DISABLED = 97; //38% alpha

    private @ColorInt int[] mColors;
    private @ColorInt int mCurrentValue;
    private boolean mCloseOnSelected = true;
    private boolean mValueSet = false;
    private View mColorView;
    private int mOutlineWidth = 0;
    private int mFixedColumnCount = -1;

    private SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if(getKey().equals(key)){
                mCurrentValue = prefs.getInt(key, mCurrentValue);
                updateColorView();
            }
        }
    };

    public SpectrumPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpectrumPreference, 0, 0);
        try {
            int id = a.getResourceId(R.styleable.SpectrumPreference_spectrum_colors, 0);
            if (id != 0) {
                mColors = getContext().getResources().getIntArray(id);
            }
            mCloseOnSelected = a.getBoolean(R.styleable.SpectrumPreference_spectrum_closeOnSelected, true);
            mOutlineWidth = a.getDimensionPixelSize(R.styleable.SpectrumPalette_spectrum_outlineWidth, 0);
            mFixedColumnCount = a.getInt(R.styleable.SpectrumPalette_spectrum_columnCount, -1);
        } finally {
            a.recycle();
        }

        setDialogLayoutResource(R.layout.dialog_color_picker);
        setWidgetLayoutResource(R.layout.color_preference_widget);

    }

    @Override
    public void onAttached() {
        super.onAttached();
        getSharedPreferences().registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    protected void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        mColorView = holder.findViewById(R.id.color_preference_widget);
        updateColorView();
    }

    /**
     * Sets the colors that will be shown in the color selection dialog.
     *
     * @param colors The colors
     */
    public void setColors(@ColorInt int[] colors) {
        mColors = colors;
    }

    /**
     * @param colorsResId Resource identifier of an array of colors
     * @see #setColors(int[])
     */
    public void setColors(@ArrayRes int colorsResId) {
        mColors = getContext().getResources().getIntArray(colorsResId);
    }

    /**
     * The colors that will be shown in the color selection dialog
     *
     * @return Array of colors
     */
    public @ColorInt int[] getColors() {
        return mColors;
    }

    /**
     * By default, the color selection dialog will close automatically when a color is
     * clicked/selected, and that selection will be saved. If you want the user to have to press
     * the positive button to confirm the color selection, you should use this method..
     *
     * @param closeOnSelected if the selection dialog should close automatically when a color is
     *                        clicked
     */
    public void setCloseOnSelected(boolean closeOnSelected) {
        mCloseOnSelected = closeOnSelected;
    }

    /**
     * @return true if the dialog will close automatically when a color is selected
     * @see #setCloseOnSelected(boolean)
     */
    public boolean getCloseOnSelected() {
        return mCloseOnSelected;
    }

    private void updateColorView() {
        if (mColorView == null) {
            return;
        }
        ColorCircleDrawable drawable = new ColorCircleDrawable(mCurrentValue);
        drawable.setOutlineWidth(mOutlineWidth);
        if (!isEnabled()) {
            // Show just a gray circle outline
            drawable.setColor(Color.WHITE);
            drawable.setAlpha(0);
            drawable.setOutlineWidth(getContext().getResources().getDimensionPixelSize(R.dimen.color_preference_disabled_outline_size));
            drawable.setOutlineColor(Color.BLACK);
            drawable.setOutlineAlpha(ALPHA_DISABLED);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mColorView.setBackground(drawable);
        } else {
            // noinspection deprecation
            mColorView.setBackgroundDrawable(drawable);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mCurrentValue = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            mCurrentValue = (Integer) defaultValue;
            persistInt(mCurrentValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    public void setValue(@ColorInt int value) {
        // Always persist/notify the first time.
        final boolean changed = mCurrentValue != value;
        if (changed || !mValueSet) {
            mCurrentValue = value;
            mValueSet = true;
            persistInt(value);
            updateColorView();
            if (changed) {
                notifyChanged();
            }
        }
    }

    public int getOutlineWidth() {
        return mOutlineWidth;
    }

    public int getFixedColumnCount() {
        return mFixedColumnCount;
    }

    @ColorInt
    public int getValue() {
        return mCurrentValue;
    }

    public static boolean onDisplayPreferenceDialog(Preference preference, PreferenceFragmentCompat target) {
        boolean handled = false;
        if (target.getTargetFragment() instanceof PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) {
            handled = ((PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) target.getTargetFragment())
                    .onPreferenceDisplayDialog(target, preference);
        }
        if (!handled && target.getActivity() instanceof PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) {
            handled = ((PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) target.getActivity())
                    .onPreferenceDisplayDialog(target, preference);
        }
        // check if dialog is already showing
        if (!handled && target.getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            handled = true;
        }

        if (!handled && preference instanceof SpectrumPreferenceCompat) {
            DialogFragment f = SpectrumPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            f.setTargetFragment(target, 0);
            f.show(target.getFragmentManager(), DIALOG_FRAGMENT_TAG);
            handled = true;
        }
        return handled;
    }
}
