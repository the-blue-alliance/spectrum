package com.thebluealliance.spectrum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.preference.DialogPreference;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.spectrum.internal.ColorCircleDrawable;

public class SpectrumPreference extends DialogPreference {

    private static final @ColorInt int DEFAULT_VALUE = Color.BLACK;
    public static final int ALPHA_DISABLED = 97; //38% alpha

    private @ColorInt int[] mColors;
    private @ColorInt int mCurrentValue;
    private @ColorInt int mDialogColor;
    private boolean mCloseOnSelected = true;
    private SpectrumPalette mColorPalette;
    private boolean mValueSet = false;
    private View mColorView;
    private int mOutlineWidth = 0;
    private @ColorInt int mOutlineColor = -1;
    private int mFixedColumnCount = -1;

    private SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if(getKey().equals(key)){
                mCurrentValue = prefs.getInt(key, mCurrentValue);
                updateColorView();
            }
        }
    };

    public SpectrumPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .SpectrumPreference, 0, 0);
        try {
            int id = a.getResourceId(R.styleable.SpectrumPreference_spectrum_colors, 0);
            if (id != 0) {
                mColors = getContext().getResources().getIntArray(id);
            }
            mCloseOnSelected = a.getBoolean(R.styleable.SpectrumPreference_spectrum_closeOnSelected, true);
            mOutlineWidth = a.getDimensionPixelSize(R.styleable.SpectrumPalette_spectrum_outlineWidth, 0);
            mOutlineColor = a.getColor(R.styleable.SpectrumPalette_spectrum_outlineColor, -1);
            mFixedColumnCount = a.getInt(R.styleable.SpectrumPalette_spectrum_columnCount, -1);
        } finally {
            a.recycle();
        }

        setDialogLayoutResource(R.layout.dialog_color_picker);
        setWidgetLayoutResource(R.layout.color_preference_widget);
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
    @ColorInt
    public int[] getColors() {
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
     * @see #setCloseOnSelected(boolean)
     * @return true if the dialog will close automatically when a color is selected
     */
    public boolean getCloseOnSelected() {
        return mCloseOnSelected;
    }

    public void setColor(@ColorInt int value) {
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

    @ColorInt
    public int getColor() {
        return mCurrentValue;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        getSharedPreferences().registerOnSharedPreferenceChangeListener(mListener);
        return super.onCreateView(parent);
    }

    @Override
    protected void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        mColorView = view.findViewById(R.id.color_preference_widget);
        updateColorView();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        if (mCloseOnSelected) {
            // Don't show the positive button; clicking a color will be the "positive" action
            builder.setPositiveButton(null, null);
        }
    }

    private void updateColorView() {
        if (mColorView == null) {
            return;
        }
        ColorCircleDrawable drawable = new ColorCircleDrawable(mCurrentValue);
        drawable.setOutlineWidth(mOutlineWidth);
        drawable.setOutlineColor(mOutlineColor);
        if (!isEnabled()) {
            // Show just a gray circle outline
            drawable.setColor(Color.BLACK);
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
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        if (mColors == null) {
            throw new RuntimeException("SpectrumPreference requires a colors array");
        }

        mColorPalette = (SpectrumPalette) view.findViewById(R.id.palette);
        mColorPalette.setColors(mColors);
        mColorPalette.setSelectedColor(mCurrentValue);
        mColorPalette.setOutlineWidth(mOutlineWidth);
        mColorPalette.setOutlineColor(mOutlineColor);
        mColorPalette.setFixedColumnCount(mFixedColumnCount);
        mColorPalette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(@ColorInt int color) {
                mDialogColor = color;
                if (mCloseOnSelected) {
                    SpectrumPreference.this.onClick(null, DialogInterface.BUTTON_POSITIVE);
                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }
                }
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            if (callChangeListener(mDialogColor)) {
                setColor(mDialogColor);
            }
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
}
