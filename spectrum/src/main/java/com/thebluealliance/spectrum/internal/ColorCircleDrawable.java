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
    private int mBorderWidth = 0;
    private final Paint mOutlinePaint;

    public ColorCircleDrawable(final @ColorInt int color) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutlinePaint.setColor(ColorUtil.isColorDark(color) ? Color.WHITE : Color.BLACK);
        mOutlinePaint.setStyle(Paint.Style.STROKE);
    }

    public void setColor(@ColorInt int color) {
        mPaint.setColor(color);
        mOutlinePaint.setColor(ColorUtil.isColorDark(color) ? Color.WHITE : Color.BLACK);
        invalidateSelf();
    }

    public void setBorderColor(@ColorInt int color) {
        mOutlinePaint.setColor(color);
        invalidateSelf();
    }

    public void setBorderAlpha(int alpha) {
        mOutlinePaint.setAlpha(alpha);
        invalidateSelf();
    }

    /**
     * Change the size of the outlining
     *
     * @param width in px
     */
    public void setBorderWidth(int width) {
        if (width < 0) {
            width = 0;
        }
        mBorderWidth = width;
        mOutlinePaint.setStrokeWidth(mBorderWidth);
        invalidateSelf();
    }

    @Override
    public void draw(final Canvas canvas) {
        final Rect bounds = getBounds();
        if (mBorderWidth != 0) {
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), mRadius - mBorderWidth, mPaint);
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), mRadius - mBorderWidth, mOutlinePaint);
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

