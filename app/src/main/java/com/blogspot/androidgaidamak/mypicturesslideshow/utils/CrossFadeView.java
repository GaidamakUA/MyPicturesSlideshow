package com.blogspot.androidgaidamak.mypicturesslideshow.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class CrossFadeView extends FrameLayout {
    private long mFadeDelay = 1000;
    private ImageView mFirst;
    private ImageView mSecond;
    private boolean mFirstShowing;
    private LinearInterpolator interpolator = new LinearInterpolator();

    public CrossFadeView(Context context) {
        super(context);
        init(context);
    }

    public CrossFadeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CrossFadeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context c) {
        mFirst = new ImageView(c);
        mFirst.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mFirst.setAlpha(1.0f);
        mFirst.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mSecond = new ImageView(c);
        mSecond.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mSecond.setAlpha(0.0f);
        mSecond.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mFirstShowing = true;

        addView(mFirst);
        addView(mSecond);
    }

    public void setFadeDelay(long fadeDelay) {
        mFadeDelay = fadeDelay;
    }

    public void showImage(Bitmap bitmap) {
        if (mFirstShowing) {
            mSecond.setImageBitmap(bitmap);
            mSecond.animate().setInterpolator(interpolator).alpha(1f).setDuration(mFadeDelay);
        } else {
            mFirst.setImageBitmap(bitmap);
            mSecond.animate().setInterpolator(interpolator).alpha(0f).setDuration(mFadeDelay);
        }

        mFirstShowing = !mFirstShowing;
    }
}