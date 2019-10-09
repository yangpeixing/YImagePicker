package com.ypx.imagepicker.bean;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.adapter.multi.BaseItemView;

import java.io.Serializable;

/**
 * Description: 选择器UI样式模型
 * <p>
 * Author: peixing.yang
 * Date: 2018/10/16 11:00
 */
public class CropUiConfig implements Serializable {
    private int themeColor;
    private int cropViewBackgroundColor;
    private int cameraIconID;
    private int cameraBackgroundColor;
    private int backIconID;
    private int backIconColor;
    private int titleBarBackgroundColor;
    private int titleTextColor;
    private int nextBtnUnSelectTextColor;
    private int nextBtnSelectedTextColor;
    private int nextBtnUnSelectBackground;
    private int nextBtnSelectedBackground;
    private int unSelectIconID;
    private int fullIconID;
    private int fitIconID;
    private int gapIconID;
    private int FillIconID;
    private int gridBackgroundColor;

    public CropUiConfig() {
        setUnSelectIconID(R.mipmap.picker_icon_unselect);
        setCameraIconID(R.mipmap.picker_ic_camera);
        setBackIconID(R.mipmap.picker_icon_close_black);
        setFitIconID(R.mipmap.picker_icon_fit);
        setFullIconID(R.mipmap.picker_icon_full);
        setGapIconID(R.mipmap.picker_icon_haswhite);
        setFillIconID(R.mipmap.picker_icon_fill);
        setBackIconColor(Color.BLACK);

        setCropViewBackgroundColor(Color.parseColor("#BBBBBB"));
        setCameraBackgroundColor(Color.TRANSPARENT);
        setThemeColor(Color.parseColor("#859D7B"));
        setTitleBarBackgroundColor(Color.parseColor("#F5F5F5"));
        setNextBtnSelectedTextColor(Color.parseColor("#859D7B"));
        setNextBtnUnSelectTextColor(Color.parseColor("#B0B0B0"));
        setTitleTextColor(Color.WHITE);
        setGridBackgroundColor(Color.parseColor("#F5F5F5"));
    }

    public int getGridBackgroundColor() {
        return gridBackgroundColor;
    }

    public void setGridBackgroundColor(int gridBackgroundColor) {
        this.gridBackgroundColor = gridBackgroundColor;
    }

    public int getThemeColor() {
        return themeColor;
    }

    public int getBackIconColor() {
        return backIconColor;
    }

    public void setBackIconColor(int backIconColor) {
        this.backIconColor = backIconColor;
    }

    /**
     * @param themeColor 主题色，包含item选中时背景色和边框色
     */
    public void setThemeColor(int themeColor) {
        this.themeColor = themeColor;
    }

    public int getCropViewBackgroundColor() {
        return cropViewBackgroundColor;
    }

    public void setCropViewBackgroundColor(int cropViewBackgroundColor) {
        this.cropViewBackgroundColor = cropViewBackgroundColor;
    }

    public int getCameraIconID() {
        return cameraIconID;
    }

    public void setCameraIconID(int cameraIconID) {
        this.cameraIconID = cameraIconID;
    }

    public int getCameraBackgroundColor() {
        return cameraBackgroundColor;
    }

    public void setCameraBackgroundColor(int cameraBackgroundColor) {
        this.cameraBackgroundColor = cameraBackgroundColor;
    }

    public int getBackIconID() {
        return backIconID;
    }

    public void setBackIconID(int backIconID) {
        this.backIconID = backIconID;
    }

    public int getTitleBarBackgroundColor() {
        return titleBarBackgroundColor;
    }

    public void setTitleBarBackgroundColor(int titleBarBackgroundColor) {
        this.titleBarBackgroundColor = titleBarBackgroundColor;
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public int getNextBtnUnSelectTextColor() {
        return nextBtnUnSelectTextColor;
    }

    public void setNextBtnUnSelectTextColor(int nextBtnUnSelectTextColor) {
        this.nextBtnUnSelectTextColor = nextBtnUnSelectTextColor;
    }

    public int getNextBtnSelectedTextColor() {
        return nextBtnSelectedTextColor;
    }

    public void setNextBtnSelectedTextColor(int nextBtnSelectedTextColor) {
        this.nextBtnSelectedTextColor = nextBtnSelectedTextColor;
    }

    public int getNextBtnUnSelectBackground() {
        return nextBtnUnSelectBackground;
    }

    public void setNextBtnUnSelectBackground(int nextBtnUnSelectBackground) {
        this.nextBtnUnSelectBackground = nextBtnUnSelectBackground;
    }

    public int getNextBtnSelectedBackground() {
        return nextBtnSelectedBackground;
    }

    public void setNextBtnSelectedBackground(int nextBtnSelectedBackground) {
        this.nextBtnSelectedBackground = nextBtnSelectedBackground;
    }

    public int getUnSelectIconID() {
        return unSelectIconID;
    }

    public void setUnSelectIconID(int unSelectIconID) {
        this.unSelectIconID = unSelectIconID;
    }

    public int getFullIconID() {
        return fullIconID;
    }

    public void setFullIconID(int fullIconID) {
        this.fullIconID = fullIconID;
    }

    public int getFitIconID() {
        return fitIconID;
    }

    public void setFitIconID(int fitIconID) {
        this.fitIconID = fitIconID;
    }

    public int getGapIconID() {
        return gapIconID;
    }

    public void setGapIconID(int gapIconID) {
        this.gapIconID = gapIconID;
    }

    public int getFillIconID() {
        return FillIconID;
    }

    public void setFillIconID(int fillIconID) {
        FillIconID = fillIconID;
    }
}
