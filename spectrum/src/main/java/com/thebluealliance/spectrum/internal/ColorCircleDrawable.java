package com.thebluealliance.spectrum.internal;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

public class ColorCircleDrawable extends Drawable {
    private final Paint mPaint;
    private int mRadius = 0;
    private int mStrokeWidth = 0;
    private final Paint mOutlinePaint;

    public ColorCircleDrawable(final @ColorInt int color) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutlinePaint.setColor(ColorUtil.isColorDark(color) ? Color.WHITE : Color.BLACK);
    }

    public void setColor(@ColorInt int color) {
        mPaint.setColor(color);
        mOutlinePaint.setColor(ColorUtil.isColorDark(color) ? Color.WHITE : Color.BLACK);
        invalidateSelf();
    }

    /**
     * Change the size of the outlining
     *
     * @param width in px
     */
    public void setStrokeWidth(int width) {
        mStrokeWidth = width;
        invalidateSelf();
    }

    @Override
    public void draw(final Canvas canvas) {
        final Rect bounds = getBounds();
        if (mStrokeWidth != 0) {
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), mRadius, mOutlinePaint);
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), mRadius - mStrokeWidth, mPaint);
        } else {
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), mRadius, mPaint);
        }
    }

    @Override
    protected void onBoundsChange(final Rect bounds) {
        super.onBoundsChange(bounds);
        mRadius = Math.min(bounds.width(), bounds.height()) / 2;
    }

    @Override
    public void setAlpha(final int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(final ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}

