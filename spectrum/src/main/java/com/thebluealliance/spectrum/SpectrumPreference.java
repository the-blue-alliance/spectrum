package com.thebluealliance.spectrum;

import com.thebluealliance.spectrum.internal.ColorCircleDrawable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.preference.DialogPreference;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

public class SpectrumPreference extends DialogPreference {

    private static final @ColorInt int DEFAULT_VALUE = Color.BLACK;

    private @ColorInt int[] mColors;
    private @ColorInt int mCurrentValue;
    private ColorPaletteView mColorPalette;
    private View mColorView;

    public SpectrumPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .SpectrumPreference, 0, 0);
        try {
            int id = a.getResourceId(R.styleable.SpectrumPreference_colors, 0);
            if (id != 0) {
                mColors = getContext().getResources().getIntArray(id);
            }
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
    public
    @ColorInt
    int[] getColors() {
        return mColors;
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

        // Don't show the positive button; clicking a color will be the "positive" action
        builder.setPositiveButton(null, null);
    }

    private void updateColorView() {
        if (mColorView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mColorView.setBackground(new ColorCircleDrawable(mCurrentValue));
        } else {
            // noinspection deprecation
            mColorView.setBackgroundDrawable(new ColorCircleDrawable(mCurrentValue));
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        if (mColors == null) {
            throw new RuntimeException("SpectrumPreference requires a colors array");
        }

        mColorPalette = (ColorPaletteView) view.findViewById(R.id.palette);
        mColorPalette.setColors(mColors);
        mColorPalette.setSelectedColor(mCurrentValue);
        mColorPalette.setOnColorSelectedListener(new ColorPaletteView.OnColorSelectedListener() {
            @Override
            public void onColorSelected(@ColorInt int color) {
                mCurrentValue = color;
                updateColorView();
                SpectrumPreference.this.onClick(null, DialogInterface.BUTTON_POSITIVE);
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(mCurrentValue);
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
