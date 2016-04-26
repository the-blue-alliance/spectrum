package com.thebluealliance.spectrum;

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

import com.thebluealliance.spectrum.internal.ColorCircleDrawable;

public class SpectrumPreference extends DialogPreference {

    private static final @ColorInt int DEFAULT_VALUE = Color.BLACK;
    public static final int ALPHA_DISABLED = 97; //38% alpha

    private @ColorInt int[] mColors;
    private @ColorInt int mCurrentValue;
    private boolean mCloseOnSelected = true;
    private SpectrumPalette mColorPalette;
    private View mColorView;
    private int mBorderWidth = 0;
    private int mFixedColumnCount = -1;

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
            mBorderWidth = a.getDimensionPixelSize(R.styleable.SpectrumPalette_spectrum_borderWidth, 0);
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
    public
    @ColorInt int[] getColors() {
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
        drawable.setBorderWidth(mBorderWidth);
        if (!isEnabled()) {
            drawable.setColor(Color.BLACK);
            drawable.setAlpha(ALPHA_DISABLED);
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
        mColorPalette.setBorderWidth(mBorderWidth);
        mColorPalette.setFixedColumnCount(mFixedColumnCount);
        mColorPalette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(@ColorInt int color) {
                mCurrentValue = color;
                updateColorView();
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
