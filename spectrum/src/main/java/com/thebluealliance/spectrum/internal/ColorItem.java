package com.thebluealliance.spectrum.internal;

import com.thebluealliance.spectrum.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ColorItem extends FrameLayout implements View.OnClickListener {

    /**
     * {@link EventBus} used internally for inter-component communication
     */
    private EventBus mEventBus;

    private ImageView mItemCheckmark;
    private @ColorInt int mColor;
    private Paint mBackgroundPaint;
    private boolean mIsSelected = false;

    public ColorItem(Context context, @ColorInt int color, boolean isSelected, EventBus eventBus) {
        super(context);

        mColor = color;
        mIsSelected = isSelected;
        mEventBus = eventBus;

        init();
        setChecked(mIsSelected);
    }

    public ColorItem(Context context) {
        super(context);
        init();
    }

    public ColorItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setForeground(createForegroundDrawable());
        setBackgroundDrawable(createBackgroundDrawable());

        mEventBus.register(this);
        setOnClickListener(this);

        LayoutInflater.from(getContext()).inflate(R.layout.color_item, this, true);
        mItemCheckmark = (ImageView) findViewById(R.id.selected_checkmark);
        mItemCheckmark.setImageResource(isDarkBackground() ? R.drawable.ic_check_white_24dp : R.drawable.ic_check_black_24dp);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mColor);
        mBackgroundPaint.setAntiAlias(true);
    }

    public void setChecked(boolean checked) {
        boolean oldChecked = mIsSelected;
        mIsSelected = checked;

        if (!oldChecked && mIsSelected) {
            // Animate checkmark appearance

            setItemCheckmarkAttributes(0.0f);
            mItemCheckmark.setVisibility(View.VISIBLE);

            mItemCheckmark.animate()
                    .alpha(1.0f)
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    setItemCheckmarkAttributes(1.0f);
                }
            }).start();
        } else if (oldChecked && !mIsSelected) {
            // Animate checkmark disappearance

            mItemCheckmark.setVisibility(View.VISIBLE);
            setItemCheckmarkAttributes(1.0f);

            mItemCheckmark.animate()
                    .alpha(0.0f)
                    .scaleX(0.0f)
                    .scaleY(0.0f)
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    mItemCheckmark.setVisibility(View.INVISIBLE);
                    setItemCheckmarkAttributes(0.0f);
                }
            }).start();
        } else {
            // Just sync the view's visibility
            updateCheckmarkVisibility();
        }
    }

    private void updateCheckmarkVisibility() {
        mItemCheckmark.setVisibility(mIsSelected ? View.VISIBLE : View.INVISIBLE);
        setItemCheckmarkAttributes(1.0f);
    }

    /**
     * Convenience method for simultaneously setting the alpha, X scale, and Y scale of a view
     *
     * @param value the value to be set
     */
    private void setItemCheckmarkAttributes(float value) {
        mItemCheckmark.setAlpha(value);
        mItemCheckmark.setScaleX(value);
        mItemCheckmark.setScaleY(value);
    }

    @Subscribe
    public void onSelectedColorChanged(SelectedColorChangedEvent event) {
        setChecked(event.getSelectedColor() == mColor);
    }

    @Override
    public void onClick(View v) {
        mEventBus.post(new SelectedColorChangedEvent(mColor));
    }

    /**
     * Computes if the background color is considered "dark"; used to determine if the foreground
     * image (the checkmark) should be white or black.
     *
     * Based on http://stackoverflow.com/a/24810681/2444312.
     *
     * @return true if the background is "dark"
     */
    private boolean isDarkBackground() {
        int r = (mColor >> 16) & 0xFF;
        int g = (mColor >> 8) & 0xFF;
        int b = mColor & 0xFF;
        double brightness = (r * 0.299) + (g * 0.587) + (b * 0.114);
        return brightness < 160;
    }

    private Drawable createBackgroundDrawable() {
        GradientDrawable mask = new GradientDrawable();
        mask.setShape(GradientDrawable.OVAL);
        mask.setColor(mColor);
        return mask;
    }

    private Drawable createForegroundDrawable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Use a ripple drawable
            GradientDrawable mask = new GradientDrawable();
            mask.setShape(GradientDrawable.OVAL);
            mask.setColor(Color.BLACK);

            return new RippleDrawable(ColorStateList.valueOf(getRippleColor(mColor)), null, mask);
        } else {
            // Use a translucent foreground
            StateListDrawable foreground = new StateListDrawable();
            foreground.setAlpha(80);
            foreground.setEnterFadeDuration(250);
            foreground.setExitFadeDuration(250);

            GradientDrawable mask = new GradientDrawable();
            mask.setShape(GradientDrawable.OVAL);
            mask.setColor(getRippleColor(mColor));
            foreground.addState(new int[]{android.R.attr.state_pressed}, mask);

            foreground.addState(new int[]{}, new ColorDrawable(Color.TRANSPARENT));

            return foreground;
        }
    }

    private int getRippleColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.5f;
        return Color.HSVToColor(hsv);
    }
}
