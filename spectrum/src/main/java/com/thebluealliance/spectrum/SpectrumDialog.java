package com.thebluealliance.spectrum;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

public class SpectrumDialog extends DialogFragment implements ColorPaletteView.OnColorSelectedListener {

    private static final String KEY_TITLE = "title";
    private static final String KEY_COLORS = "colors";
    private static final String KEY_SELECTED_COLOR = "selected_color";
    private static final String KEY_ORIGINAL_SELECTED_COLOR = "origina_selected_color";
    private static final String KEY_SHOULD_DISMISS_ON_COLOR_SELECTED = "should_dismiss_on_color_selected";
    private static final String KEY_POSITIVE_BUTTON_TEXT = "positive_button_text";
    private static final String KEY_NEGATIVE_BUTTON_TEXT = "negative_button_text";

    private CharSequence mTitle;
    private CharSequence mPositiveButtonText;
    private CharSequence mNegativeButtonText;
    private @ColorInt int[] mColors;
    private @ColorInt int mOriginalSelectedColor = -1;
    private @ColorInt int mSelectedColor = -1;
    private boolean mShouldDismissOnColorSelected = true;
    private ColorPickerListener mListener;

    public SpectrumDialog() {
        // Required empty constructor
    }

    public static class Builder {
        private Context mContext;
        private Bundle mArgs;
        private ColorPickerListener mListener;

        public Builder(Context context) {
            mContext = context;
            mArgs = new Bundle();
        }

        /**
         * Sets the dialog's title.
         *
         * @return This {@link Builder} for method chaining
         */
        public Builder setTitle(CharSequence title) {
            mArgs.putCharSequence(KEY_TITLE, title);
            return this;
        }

        /**
         * Sets the dialog's title using the given resource ID.
         *
         * @return This {@link Builder} for method chaining
         */
        public Builder setTitle(@StringRes int titleResId) {
            mArgs.putCharSequence(KEY_TITLE, mContext.getText(titleResId));
            return this;
        }

        /**
         * Sets the text for the dialog's positive button.
         *
         * Note that the positive button is only shown if you call
         * {@link #setDismissOnColorSelected(boolean)} to tell the dialog to not dismiss
         * automatically when the user selects a color.
         *
         * @return This {@link Builder} for method chaining
         */
        public Builder setPositiveButtonText(CharSequence text) {
            mArgs.putCharSequence(KEY_POSITIVE_BUTTON_TEXT, text);
            return this;
        }

        /**
         * Sets the text for the dialog's positive button from the given resource ID.
         *
         * Note that the positive button is only shown if you call
         * {@link #setDismissOnColorSelected(boolean)} to tell the dialog to not dismiss
         * automatically when the user selects a color.
         *
         * @return This {@link Builder} for method chaining
         */
        public Builder setPositiveButtonText(@StringRes int textRes) {
            mArgs.putCharSequence(KEY_POSITIVE_BUTTON_TEXT, mContext.getText(textRes));
            return this;
        }

        /**
         * Sets the text for the dialog's negative button.
         *
         * @return This {@link Builder} for method chaining
         */
        public Builder setNegativeButtonText(CharSequence text) {
            mArgs.putCharSequence(KEY_NEGATIVE_BUTTON_TEXT, text);
            return this;
        }

        /**
         * Sets the text for the dialog's negative button from the given resource ID.
         *
         * @return This {@link Builder} for method chaining
         */
        public Builder setNegativeButtonText(@StringRes int textRes) {
            mArgs.putCharSequence(KEY_NEGATIVE_BUTTON_TEXT, mContext.getText(textRes));
            return this;
        }

        /**
         * Sets the colors that will be offered to the user as choices in the dialog.
         *
         * @return This {@link Builder} for method chaining
         */
        public Builder setColors(@ColorInt int[] colors) {
            mArgs.putIntArray(KEY_COLORS, colors);
            return this;
        }

        /**
         * Sets the colors that will be offered to the user as choices in the dialog. The
         * referenced array resource should be composed of colors or references to colors.
         *
         * @return This {@link Builder} for method chaining
         */
        public Builder setColors(@ArrayRes int colorsArrayRes) {
            mArgs.putIntArray(KEY_COLORS, mContext.getResources().getIntArray(colorsArrayRes));
            return this;
        }

        /**
         * Sets the color that will be selected when the dialog is first shown. If none is
         * specified, the default will be the first color in the array of colors set via
         * {@link #setColors(int[])} or {@link #setColors(int)}. Note that the specified color
         * should be present in this dialog's array of colors; behavior is undefined otherwise.
         *
         * @return This {@link Builder} for method chaining
         */
        public Builder setSelectedColor(@ColorInt int selectedColor) {
            mArgs.putInt(KEY_SELECTED_COLOR, selectedColor);
            mArgs.putInt(KEY_ORIGINAL_SELECTED_COLOR, selectedColor);
            return this;
        }

        /**
         * Sets the color that will be selected when the dialog is first shown. If none is
         * specified, the default will be the first color in the array of colors set via
         * {@link #setColors(int[])} or {@link #setColors(int)}. Note that the specified color
         * should be present in this dialog's array of colors; behavior is undefined otherwise.
         *
         * @return This {@link Builder} for method chaining
         */
        public Builder setSelectedColorRes(@ColorRes int selectedColorRes) {
            mArgs.putInt(KEY_SELECTED_COLOR, mContext.getResources().getColor(selectedColorRes));
            mArgs.putInt(KEY_ORIGINAL_SELECTED_COLOR, mContext.getResources().getColor(selectedColorRes));
            return this;
        }

        /**
         * Sets a listener to receive callbacks when the user interacts with the dialog.
         *
         * If you want this dialog to work properly across orientation changes, you should call
         * {@link SpectrumDialog#setColorPickerListener(ColorPickerListener)} when your activity
         * is recreated. The dialog will persist its initial configuration and state across
         * configuration changes, but it cannot retain the callback object
         * (see {@link SpectrumDialog#onSaveInstanceState(Bundle)}).
         *
         * @return This {@link Builder} for method chaining
         * @see ColorPickerListener
         */
        public Builder setColorSelectedListener(ColorPickerListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * Sets if the dialog should close automatically when a color is selected. By default,
         * clicking a color will close the dialog and invoke {@link ColorPickerListener#onColorSelected(int)}.
         * If you want the dialog close to be deferred until the user presses the dialog's positive
         * button, you should use this method.
         *
         * @return This {@link Builder} for method chaining
         */
        public Builder setDismissOnColorSelected(boolean dismiss) {
            mArgs.putBoolean(KEY_SHOULD_DISMISS_ON_COLOR_SELECTED, dismiss);
            return this;
        }

        public SpectrumDialog build() {
            SpectrumDialog dialog = new SpectrumDialog();
            dialog.setArguments(mArgs);
            dialog.setColorPickerListener(mListener);
            return dialog;
        }
    }

    /**
     * Sets a callback to be notified of when the user interacts with the dialog, either by
     * confirming a color selection or by cancelling the dialog.
     *
     * @param listener object on which the callback methods will be invoked
     * @see ColorPickerListener
     */
    public void setColorPickerListener(ColorPickerListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // First, initialize values from the arguments, if present
        Bundle args = getArguments();

        if (args != null && args.containsKey(KEY_TITLE)) {
            mTitle = args.getCharSequence(KEY_TITLE);
        } else {
            mTitle = getContext().getText(R.string.default_dialog_title);
        }

        if (args != null && args.containsKey(KEY_COLORS)) {
            mColors = args.getIntArray(KEY_COLORS);
        } else {
            // Default to a single color, black
            mColors = new int[]{Color.BLACK};
        }

        if (mColors == null || mColors.length == 0) {
            throw new IllegalArgumentException("SpectrumDialog must be created with an array of colors");
        }

        if (args != null && args.containsKey(KEY_SELECTED_COLOR)) {
            mSelectedColor = args.getInt(KEY_SELECTED_COLOR);
        } else {
            // Default to the first item of the color array
            mSelectedColor = mColors[0];
        }

        if (args != null && args.containsKey(KEY_ORIGINAL_SELECTED_COLOR)) {
            mOriginalSelectedColor = args.getInt(KEY_ORIGINAL_SELECTED_COLOR);
        } else {
            mOriginalSelectedColor = mSelectedColor;
        }

        if (args != null && args.containsKey(KEY_SHOULD_DISMISS_ON_COLOR_SELECTED)) {
            mShouldDismissOnColorSelected = args.getBoolean(KEY_SHOULD_DISMISS_ON_COLOR_SELECTED);
        } else {
            mShouldDismissOnColorSelected = true;
        }

        if (args != null && args.containsKey(KEY_POSITIVE_BUTTON_TEXT)) {
            mPositiveButtonText = args.getCharSequence(KEY_POSITIVE_BUTTON_TEXT);
        } else {
            mPositiveButtonText = getContext().getText(R.string.ok);
        }

        if (args != null && args.containsKey(KEY_NEGATIVE_BUTTON_TEXT)) {
            mNegativeButtonText = args.getCharSequence(KEY_NEGATIVE_BUTTON_TEXT);
        } else {
            mNegativeButtonText = getContext().getText(R.string.cancel);
        }

        // Next, overwrite any appropriate values if present in the saved instance state
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SELECTED_COLOR)) {
            mSelectedColor = savedInstanceState.getInt(KEY_SELECTED_COLOR);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_COLOR, mSelectedColor);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(mTitle);

        // Only show the positive button if the dialog is setup to not dismiss automatically
        if (mShouldDismissOnColorSelected) {
            builder.setPositiveButton(null, null);
        } else {
            builder.setPositiveButton(mPositiveButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mListener != null) {
                        mListener.onColorPickerResult(ColorPickerListener.POSITIVE, mSelectedColor);
                    }
                    dialog.dismiss();
                }
            });
        }
        builder.setNegativeButton(mNegativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onColorPickerResult(ColorPickerListener.NEGATIVE, mOriginalSelectedColor);
                }
                dialog.dismiss();
            }
        });

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_picker, null);
        ColorPaletteView palette = (ColorPaletteView) view.findViewById(R.id.palette);
        palette.setColors(mColors);
        palette.setSelectedColor(mSelectedColor);
        palette.setOnColorSelectedListener(this);

        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (mListener != null) {
            mListener.onColorPickerResult(ColorPickerListener.NEGATIVE, mOriginalSelectedColor);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener = null;
    }

    @Override
    public void onColorSelected(@ColorInt int color) {
        mSelectedColor = color;
        if (mShouldDismissOnColorSelected) {
            dismiss();
        }
    }

    /**
     *
     */
    public interface ColorPickerListener {
        /**
         * Indicates that the user confirmed the color selection, either by pressing "OK" or
         * clicking on a color.
         */
        int POSITIVE = -1;

        /**
         * Indicates that the user wants to dismiss any changes made in the dialog, which would
         * be caused by pressing the dialog's negative button, by tapping outside the dialog, or
         * by pressing the system "Back" button.
         */
        int NEGATIVE = -2;

        /**
         * @see #POSITIVE
         * @see #NEGATIVE
         */
        void onColorPickerResult(int resultCode, @ColorInt int color);
    }
}
