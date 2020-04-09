package com.ypx.imagepicker.bean.selectconfig;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Size;

import com.ypx.imagepicker.widget.cropimage.Info;

/**
 * Time: 2019/10/27 18:53
 * Author:ypx
 * Description: 单图剪裁配置类
 */
public class CropConfigParcelable implements Parcelable {
    //充满式剪裁
    public static final int STYLE_FILL = 1;
    //留白式剪裁
    public static final int STYLE_GAP = 2;
    private int cropRatioX = 1;
    private int cropRatioY = 1;
    private boolean isCircle = false;
    private int cropRectMargin = 0;
    private int cropStyle = STYLE_FILL;
    private int cropGapBackgroundColor = Color.BLACK;

    private boolean saveInDCIM = false;

    // private Size outPutSize;
    private long maxOutPutByte;
    private boolean isLessOriginalByte;
    private Info cropRestoreInfo;
    private boolean isSingleCropCutNeedTop = false;

    public boolean isSingleCropCutNeedTop() {
        return isSingleCropCutNeedTop;
    }

    public void setSingleCropCutNeedTop(boolean singleCropCutNeedTop) {
        isSingleCropCutNeedTop = singleCropCutNeedTop;
    }

    protected CropConfigParcelable() {

    }

    protected CropConfigParcelable(Parcel in) {
        cropRatioX = in.readInt();
        cropRatioY = in.readInt();
        isCircle = in.readByte() != 0;
        cropRectMargin = in.readInt();
        cropStyle = in.readInt();
        cropGapBackgroundColor = in.readInt();
        saveInDCIM = in.readByte() != 0;
        maxOutPutByte = in.readLong();
        isLessOriginalByte = in.readByte() != 0;
        cropRestoreInfo = in.readParcelable(Info.class.getClassLoader());
        isSingleCropCutNeedTop=in.readByte() != 0;
    }

    public static final Creator<CropConfigParcelable> CREATOR = new Creator<CropConfigParcelable>() {
        @Override
        public CropConfigParcelable createFromParcel(Parcel in) {
            return new CropConfigParcelable(in);
        }

        @Override
        public CropConfigParcelable[] newArray(int size) {
            return new CropConfigParcelable[size];
        }
    };


    public long getMaxOutPutByte() {
        return maxOutPutByte;
    }

    public void setMaxOutPutByte(long maxOutPutByte) {
        this.maxOutPutByte = maxOutPutByte;
    }

    public boolean isLessOriginalByte() {
        return isLessOriginalByte;
    }

    public void setLessOriginalByte(boolean lessOriginalByte) {
        isLessOriginalByte = lessOriginalByte;
    }

    public Info getCropRestoreInfo() {
        return cropRestoreInfo;
    }

    public void setCropRestoreInfo(Info cropRestoreInfo) {
        this.cropRestoreInfo = cropRestoreInfo;
    }

    public boolean isSaveInDCIM() {
        return saveInDCIM;
    }

    public void saveInDCIM(boolean saveInDCIM) {
        this.saveInDCIM = saveInDCIM;
    }

    public int getCropStyle() {
        return cropStyle;
    }

    public void setCropStyle(int cropStyle) {
        this.cropStyle = cropStyle;
    }

    public int getCropGapBackgroundColor() {
        return cropGapBackgroundColor;
    }

    public void setCropGapBackgroundColor(int cropGapBackgroundColor) {
        this.cropGapBackgroundColor = cropGapBackgroundColor;
    }

    public boolean isCircle() {
        return isCircle;
    }

    public void setCircle(boolean circle) {
        isCircle = circle;
    }


    public int getCropRectMargin() {
        return cropRectMargin;
    }

    public void setCropRectMargin(int cropRectMargin) {
        this.cropRectMargin = cropRectMargin;
    }

    public int getCropRatioX() {
        if (isCircle) {
            return 1;
        }
        return cropRatioX;
    }

    public void setCropRatio(int x, int y) {
        this.cropRatioX = x;
        this.cropRatioY = y;
    }

    public int getCropRatioY() {
        if (isCircle) {
            return 1;
        }
        return cropRatioY;
    }

    public boolean isGap() {
        return cropStyle == STYLE_GAP;
    }

    public boolean isNeedPng() {
        return isCircle || getCropGapBackgroundColor() == Color.TRANSPARENT;
    }

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
        dest.writeInt(cropRatioX);
        dest.writeInt(cropRatioY);
        dest.writeByte((byte) (isCircle ? 1 : 0));
        dest.writeInt(cropRectMargin);
        dest.writeInt(cropStyle);
        dest.writeInt(cropGapBackgroundColor);
        dest.writeByte((byte) (saveInDCIM ? 1 : 0));
        dest.writeLong(maxOutPutByte);
        dest.writeByte((byte) (isLessOriginalByte ? 1 : 0));
        dest.writeParcelable(cropRestoreInfo, flags);
        dest.writeByte((byte) (isSingleCropCutNeedTop ? 1 : 0));
    }
}
