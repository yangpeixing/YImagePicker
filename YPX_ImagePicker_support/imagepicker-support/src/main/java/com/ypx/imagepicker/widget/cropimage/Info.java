package com.ypx.imagepicker.widget.cropimage;

import android.graphics.PointF;
import android.graphics.RectF;
import android.widget.ImageView;

/**
 * Description: 图片基本信息
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class Info {

    // 内部图片在整个手机界面的位置
    public RectF mRect = new RectF();

    // 控件在窗口的位置
    public RectF mImgRect = new RectF();

    public RectF mWidgetRect = new RectF();

    public RectF mBaseRect = new RectF();

    public PointF mScreenCenter = new PointF();

    public float mScale;

    public float mDegrees;

    public float mCropX;
    public float mCropY;

    public float transitX;

    public float transitY;

    public ImageView.ScaleType mScaleType;

    public Info(RectF rect, RectF img, RectF widget, RectF base, PointF screenCenter,
                float scale, float degrees, ImageView.ScaleType scaleType) {
        mRect.set(rect);
        mImgRect.set(img);
        mWidgetRect.set(widget);
        mScale = scale;
        mScaleType = scaleType;
        mDegrees = degrees;
        mBaseRect.set(base);
        mScreenCenter.set(screenCenter);
    }

    public Info(RectF rect, RectF img, RectF widget, RectF base, PointF screenCenter, float scale, float degrees, ImageView.ScaleType scaleType, float mCropX,
                float mCropY, float transitX, float transitY) {
        mRect.set(rect);
        mImgRect.set(img);
        mWidgetRect.set(widget);
        mScale = scale;
        mScaleType = scaleType;
        mDegrees = degrees;
        mBaseRect.set(base);
        mScreenCenter.set(screenCenter);
        this.mCropX = mCropX;
        this.mCropY = mCropY;
        this.transitX = transitX;
        this.transitY = transitY;
    }


}