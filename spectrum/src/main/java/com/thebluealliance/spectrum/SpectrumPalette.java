package com.thebluealliance.spectrum;

import com.thebluealliance.spectrum.internal.ColorItem;
import com.thebluealliance.spectrum.internal.ColorUtil;
import com.thebluealliance.spectrum.internal.SelectedColorChangedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * General-purpose class that displays colors in a grid.
 */
public class SpectrumPalette extends LinearLayout {

    private static final int DEFAULT_COLUMN_COUNT = 4;

    private int mColorItemDimension;
    private int mColorItemMargin;
    private @ColorInt int[] mColors;
    private @ColorInt int mSelectedColor;
    private OnColorSelectedListener mListener;
    private boolean mAutoPadding = false;
    private boolean mHasFixedColumnCount = false;
    private int mFixedColumnCount = -1;
    private int mOutlineWidth = 0;
    private @ColorInt int mOutlineColor = -1;
    private int mComputedVerticalPadding = 0;
    private int mOriginalPaddingTop = 0;
    private int mOriginalPaddingBottom = 0;
    private boolean mSetPaddingCalledInternally = false;

    private int mNumColumns = 2;
    private int mOldNumColumns = -1;
    private boolean mViewInitialized = false;

    private EventBus mEventBus;

    private List<ColorItem> mItems = new ArrayList<>();

    public SpectrumPalette(Context context) {
        super(context);
        init();
    }

    public SpectrumPalette(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SpectrumPalette, 0, 0);

        int id = a.getResourceId(R.styleable.SpectrumPalette_spectrum_colors, 0);
        if (id != 0) {
            mColors = getContext().getResources().getIntArray(id);
        }

        mAutoPadding = a.getBoolean(R.styleable.SpectrumPalette_spectrum_autoPadding, false);
        mOutlineWidth = a.getDimensionPixelSize(R.styleable.SpectrumPalette_spectrum_outlineWidth, 0);
        mOutlineColor = a.getColor(R.styleable.SpectrumPalette_spectrum_outlineColor, -1);
        mFixedColumnCount = a.getInt(R.styleable.SpectrumPalette_spectrum_columnCount, -1);
        if (mFixedColumnCount != -1) {
            mHasFixedColumnCount = true;
        }

        a.recycle();

        mOriginalPaddingTop = getPaddingTop();
        mOriginalPaddingBottom = getPaddingBottom();

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
        mViewInitialized = false;
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        if (!mHasFixedColumnCount) {
            if (widthMode == MeasureSpec.EXACTLY) {
                width = widthSize;
                mNumColumns = computeColumnCount(widthSize - (getPaddingLeft() + getPaddingRight()));
            } else if (widthMode == MeasureSpec.AT_MOST) {
                width = widthSize;
                mNumColumns = computeColumnCount(widthSize - (getPaddingLeft() + getPaddingRight()));
            } else {
                width = computeWidthForNumColumns(DEFAULT_COLUMN_COUNT) + getPaddingLeft() + getPaddingRight();
                mNumColumns = DEFAULT_COLUMN_COUNT;
            }
        } else {
            width = computeWidthForNumColumns(mFixedColumnCount) + getPaddingLeft() + getPaddingRight();
            mNumColumns = mFixedColumnCount;
        }

        mComputedVerticalPadding = (width - (computeWidthForNumColumns(mNumColumns) + getPaddingLeft() + getPaddingRight())) / 2;

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            int desiredHeight = computeHeight(mNumColumns) + mOriginalPaddingTop + mOriginalPaddingBottom;
            if (mAutoPadding) {
                desiredHeight += (2 * mComputedVerticalPadding);
            }
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = computeHeight(mNumColumns) + mOriginalPaddingTop + mOriginalPaddingBottom;
            if (mAutoPadding) {
                height += (2 * mComputedVerticalPadding);
            }
        }

        if (mAutoPadding) {
            setPaddingInternal(getPaddingLeft(), mOriginalPaddingTop + mComputedVerticalPadding, getPaddingRight(), mOriginalPaddingBottom + mComputedVerticalPadding);
        }
        createPaletteView();

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    private int computeColumnCount(int maxWidth) {
        int numColumns = 0;
        while (((numColumns + 1) * mColorItemDimension) + ((numColumns + 1) * 2 * mColorItemMargin) <= maxWidth) {
            numColumns++;
        }
        return numColumns;
    }

    private int computeWidthForNumColumns(int columnCount) {
        return columnCount * (mColorItemDimension + 2 * mColorItemMargin);
    }

    private int computeHeight(int columnCount) {
        if (mColors == null) {
            // View does not have any colors to display, so we won't take up any room
            return 0;
        }
        int rowCount = mColors.length / columnCount;
        if (mColors.length % columnCount != 0) {
            rowCount++;
        }
        return rowCount * (mColorItemDimension + 2 * mColorItemMargin);
    }


    private void setPaddingInternal(int left, int top, int right, int bottom) {
        mSetPaddingCalledInternally = true;
        setPadding(left, top, right, bottom);
    }

    @Override public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        if (!mSetPaddingCalledInternally) {
            mOriginalPaddingTop = top;
            mOriginalPaddingBottom = bottom;
        }
    }

    private int getOriginalPaddingTop() {
        return mOriginalPaddingTop;
    }

    private int getOriginalPaddingBottom() {
        return mOriginalPaddingBottom;
    }

    /**
     * Generates the views to represent this palette's colors. The grid is implemented with
     * {@link LinearLayout}s. This class itself subclasses {@link LinearLayout} and is set up in
     * the vertical orientation. Rows consist of horizontal {@link LinearLayout}s which themselves
     * hold views that display the individual colors.
     */
    protected void createPaletteView() {
        // Only create the view if it hasn't been created yet or if the number of columns has changed
        if (mViewInitialized && mNumColumns == mOldNumColumns) {
            return;
        }
        mViewInitialized = true;
        mOldNumColumns = mNumColumns;

        removeAllViews();

        if (mColors == null) {
            return;
        }

        // Add rows
        int numItemsInRow = 0;

        LinearLayout row = createRow();
        for (int i = 0; i < mColors.length; i++) {
            View colorItem = createColorItem(mColors[i], mSelectedColor);
            row.addView(colorItem);
            numItemsInRow++;

            if (numItemsInRow == mNumColumns) {
                addView(row);
                row = createRow();
                numItemsInRow = 0;
            }
        }

        if (numItemsInRow > 0) {
            while (numItemsInRow < mNumColumns) {
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mColorItemDimension, mColorItemDimension);
        params.setMargins(mColorItemMargin, mColorItemMargin, mColorItemMargin, mColorItemMargin);
        view.setLayoutParams(params);
        if (mOutlineWidth != 0) {
            view.setOutlineWidth(mOutlineWidth);
        }
        if (mOutlineColor > -1) {
            view.setOutlineColor(mOutlineColor);
        }
        mItems.add(view);
        return view;
    }

    private ImageView createSpacer() {
        ImageView view = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mColorItemDimension, mColorItemDimension);
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

    /**
     * Returns true if for the given color a dark checkmark is used.
     *
     * @return true if color is "dark"
     */
    public boolean usesDarkCheckmark(@ColorInt int color) {
        return ColorUtil.isColorDark(color);
    }

    /**
     * Change the size of the outlining
     *
     * @param width in px
     */
    public void setOutlineWidth(int width) {
        mOutlineWidth = width;
        for (ColorItem item : mItems) {
            item.setOutlineWidth(width);
        }
    }

     /**
     * Change the color of the outlining
     *
     * @param color
     */
    public void setOutlineColor(@ColorInt int color) {
        mOutlineColor = color;
        for (ColorItem item : mItems) {
            item.setOutlineColor(color);
        }
    }
    
    
    /**
     * Tells the palette to use a fixed number of columns during layout.
     *
     * @param columnCount how many columns to use
     */
    public void setFixedColumnCount(int columnCount) {
        if (columnCount > 0) {
            Log.d("spectrum", "set column count to " + columnCount);
            mHasFixedColumnCount = true;
            mFixedColumnCount = columnCount;
            requestLayout();
            invalidate();
        } else {
            mHasFixedColumnCount = false;
            mFixedColumnCount = -1;
            requestLayout();
            invalidate();
        }
    }

}
