package com.thebluealliance.spectrum;

import com.thebluealliance.spectrum.internal.ColorItem;
import com.thebluealliance.spectrum.internal.SelectedColorChangedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;

/**
 * General-purpose class that displays colors in a grid.
 */
public class ColorPaletteView extends LinearLayout {

    private int mColorItemDimension;
    private int mColorItemMargin;
    private @ColorInt int[] mColors;
    private @ColorInt int mSelectedColor;
    private OnColorSelectedListener mListener;

    private EventBus mEventBus;

    public ColorPaletteView(Context context) {
        super(context);
        init();
    }

    public ColorPaletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mEventBus = new EventBus();
        mEventBus.register(this);

        mColorItemDimension = getResources().getDimensionPixelSize(R.dimen.color_item_small);
        mColorItemMargin = getResources().getDimensionPixelSize(R.dimen.color_item_margins_small);

        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * Sets the colors that this palette will display
     *
     * @param colors an array of ARGB colors
     */
    public void setColors(@ColorInt int[] colors) {
        mColors = colors;
        createPaletteView();
    }

    /**
     * Sets the currently selected color. This should be one of the colors specified via
     * {@link #setColors(int[])}; behavior is undefined if {@code color} is not among those colors.
     *
     * @param color the color to be marked as selected
     */
    public void setSelectedColor(@ColorInt int color) {
        mSelectedColor = color;
        mEventBus.post(new SelectedColorChangedEvent(mSelectedColor));
    }

    /**
     * Registers a callback to be invoked when a new color is selected.
     */
    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        mListener = listener;
    }

    /**
     * Generates the views to represent this palette's colors. The grid is implemented with
     * {@link LinearLayout}s. This class itself subclasses {@link LinearLayout} and is set up in
     * the vertical orientation. Rows consist of horizontal {@link LinearLayout}s which themselves
     * hold a number of {@link ColorItem}s, which display the individual colors.
     */
    protected void createPaletteView() {
        removeAllViews();

        if (mColors == null) {
            return;
        }

        // Add rows
        int maxRowLength = 5;
        int numItemsInRow = 0;

        LinearLayout row = createRow();
        for (int i = 0; i < mColors.length; i++) {
            View colorItem = createColorItem(mColors[i], mSelectedColor);
            row.addView(colorItem);
            numItemsInRow++;

            if (numItemsInRow == maxRowLength) {
                addView(row);
                row = createRow();
                numItemsInRow = 0;
            }
        }

        if (numItemsInRow > 0) {
            while (numItemsInRow < maxRowLength) {
                row.addView(createSpacer());
                numItemsInRow++;
            }
            addView(row);
        }
    }

    private LinearLayout createRow() {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(params);
        row.setGravity(Gravity.CENTER_HORIZONTAL);
        return row;
    }

    private ColorItem createColorItem(int color, int selectedColor) {
        ColorItem view = new ColorItem(getContext(), color, color == selectedColor, mEventBus);
        TableRow.LayoutParams params = new TableRow.LayoutParams(mColorItemDimension, mColorItemDimension);
        params.setMargins(mColorItemMargin, mColorItemMargin, mColorItemMargin, mColorItemMargin);
        view.setLayoutParams(params);
        return view;
    }

    private ImageView createSpacer() {
        ImageView view = new ImageView(getContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams(mColorItemDimension, mColorItemDimension);
        params.setMargins(mColorItemMargin, mColorItemMargin, mColorItemMargin, mColorItemMargin);
        view.setLayoutParams(params);
        return view;
    }

    @Subscribe
    public void onSelectedColorChanged(SelectedColorChangedEvent event) {
        mSelectedColor = event.getSelectedColor();
        if (mListener != null) {
            mListener.onColorSelected(mSelectedColor);
        }
    }

    public interface OnColorSelectedListener {
        void onColorSelected(@ColorInt int color);
    }
}
