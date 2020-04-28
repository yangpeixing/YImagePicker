package com.ypx.imagepicker.widget.cropimage;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Description: 图片基本信息
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class Info implements Parcelable, Serializable {
    // 控件在窗口的位置
    public RectF mImgRect = new RectF();
    public RectF mWidgetRect = new RectF();

    public float mDegrees;
    public float mCropX;
    public float mCropY;
    public String mScaleType;

    public float transitX;
    public float transitY;
    public float mScale;

    public ImageView.ScaleType getScaleType() {
        return ImageView.ScaleType.valueOf(mScaleType);
    }


    public Info(RectF img, RectF widget, float degrees, String scaleType, float mCropX,
                float mCropY, float transitX, float transitY, float mScale) {
        mImgRect.set(img);
        mWidgetRect.set(widget);
        mScaleType = scaleType;
        mDegrees = degrees;
        this.mCropX = mCropX;
        this.mCropY = mCropY;
        this.transitX = transitX;
        this.transitY = transitY;
        this.mScale = mScale;
    }

    protected Info(Parcel in) {
        mImgRect = in.readParcelable(RectF.class.getClassLoader());
        mWidgetRect = in.readParcelable(RectF.class.getClassLoader());
        mScaleType = in.readString();
        mDegrees = in.readFloat();
        mCropX = in.readFloat();
        mCropY = in.readFloat();
        this.transitX = in.readFloat();;
        this.transitY = in.readFloat();;
        this.mScale = in.readFloat();;
    }

    public static final Creator<Info> CREATOR = new Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mImgRect, flags);
        dest.writeParcelable(mWidgetRect, flags);
        dest.writeString(mScaleType);
        dest.writeFloat(mDegrees);
        dest.writeFloat(mCropX);
        dest.writeFloat(mCropY);
        dest.writeFloat(transitX);
        dest.writeFloat(transitY);
        dest.writeFloat(mScale);
    }
}