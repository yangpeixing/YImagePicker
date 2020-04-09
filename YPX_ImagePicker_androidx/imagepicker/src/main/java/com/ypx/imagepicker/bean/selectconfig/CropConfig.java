package com.ypx.imagepicker.bean.selectconfig;

import android.graphics.Color;
import android.util.Size;

import com.ypx.imagepicker.widget.cropimage.Info;

/**
 * Time: 2019/10/27 18:53
 * Author:ypx
 * Description: 单图剪裁配置类
 */
public class CropConfig extends BaseSelectConfig {
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

    private Size outPutSize;
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

    public Size getOutPutSize() {
        return outPutSize;
    }

    public void setOutPutSize(Size outPutSize) {
        this.outPutSize = outPutSize;
    }

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

    public CropConfigParcelable getCropInfo() {
        CropConfigParcelable parcelable = new CropConfigParcelable();
        parcelable.setCircle(isCircle);
        parcelable.setCropGapBackgroundColor(getCropGapBackgroundColor());
        parcelable.setCropRatio(getCropRatioX(), getCropRatioY());
        parcelable.setCropRectMargin(getCropRectMargin());
        parcelable.setCropRestoreInfo(getCropRestoreInfo());
        parcelable.setCropStyle(getCropStyle());
        parcelable.setLessOriginalByte(isLessOriginalByte());
        parcelable.setMaxOutPutByte(getMaxOutPutByte());
        parcelable.saveInDCIM(isSaveInDCIM());
        return parcelable;
    }
}
