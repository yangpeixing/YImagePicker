package com.ypx.imagepicker.bean;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.ypx.imagepicker.R;

import java.io.Serializable;

/**
 * Description: 小红书剪裁UI样式模型
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
    private Drawable nextBtnUnSelectBackground;
    private Drawable nextBtnSelectedBackground;
    private int unSelectIconID;
    private int fullIconID;
    private int fitIconID;
    private int gapIconID;
    private int FillIconID;
    private int videoPauseIconID;
    private int gridBackgroundColor;
    private boolean isShowNextCount;
    private String nextBtnText;

    public CropUiConfig() {
        setDefaultStyle();
    }

    //设置默认样式
    private void setDefaultStyle() {
        setUnSelectIconID(R.mipmap.picker_icon_unselect);
        setCameraIconID(R.mipmap.picker_ic_camera);
        setBackIconID(R.mipmap.picker_icon_close_black);
        setFitIconID(R.mipmap.picker_icon_fit);
        setFullIconID(R.mipmap.picker_icon_full);
        setGapIconID(R.mipmap.picker_icon_haswhite);
        setFillIconID(R.mipmap.picker_icon_fill);
        setVideoPauseIconID(R.mipmap.video_play_small);
        setBackIconColor(Color.BLACK);
        setCropViewBackgroundColor(Color.parseColor("#BBBBBB"));
        setCameraBackgroundColor(Color.TRANSPARENT);
        setThemeColor(Color.parseColor("#859D7B"));
        setTitleBarBackgroundColor(Color.parseColor("#F5F5F5"));
        setNextBtnSelectedTextColor(Color.parseColor("#859D7B"));
        setNextBtnUnSelectTextColor(Color.parseColor("#B0B0B0"));
        setTitleTextColor(Color.BLACK);
        setGridBackgroundColor(Color.parseColor("#F5F5F5"));
        setNextBtnSelectedBackground(null);
        setNextBtnUnSelectBackground(null);
        setShowNextCount(true);
        setNextBtnText("下一步");
    }

    public int getVideoPauseIconID() {
        return videoPauseIconID;
    }

    public void setVideoPauseIconID(int videoPauseIconID) {
        this.videoPauseIconID = videoPauseIconID;
    }

    public boolean isShowNextCount() {
        return isShowNextCount;
    }

    /**
     * @param showNextCount 设置下一步数字图标是否显示
     */
    public void setShowNextCount(boolean showNextCount) {
        isShowNextCount = showNextCount;
    }

    public String getNextBtnText() {
        return nextBtnText;
    }

    public void setNextBtnText(String nextBtnText) {
        this.nextBtnText = nextBtnText;
    }

    public int getGridBackgroundColor() {
        return gridBackgroundColor;
    }

    /**
     * @param gridBackgroundColor 设置列表背景色
     */
    public void setGridBackgroundColor(int gridBackgroundColor) {
        this.gridBackgroundColor = gridBackgroundColor;
    }

    public int getThemeColor() {
        return themeColor;
    }

    public int getBackIconColor() {
        return backIconColor;
    }

    /**
     * @param backIconColor 设置返回按钮的颜色
     */
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

    /**
     * @param cropViewBackgroundColor 设置剪裁区域背景色
     */
    public void setCropViewBackgroundColor(int cropViewBackgroundColor) {
        this.cropViewBackgroundColor = cropViewBackgroundColor;
    }

    public int getCameraIconID() {
        return cameraIconID;
    }

    /**
     * @param cameraIconID 设置拍照图标
     */
    public void setCameraIconID(int cameraIconID) {
        this.cameraIconID = cameraIconID;
    }

    public int getCameraBackgroundColor() {
        return cameraBackgroundColor;
    }

    /**
     * @param cameraBackgroundColor 设置拍照item的背景色
     */
    public void setCameraBackgroundColor(int cameraBackgroundColor) {
        this.cameraBackgroundColor = cameraBackgroundColor;
    }

    public int getBackIconID() {
        return backIconID;
    }

    /**
     * @param backIconID 设置返回按钮图标
     */
    public void setBackIconID(int backIconID) {
        this.backIconID = backIconID;
    }

    public int getTitleBarBackgroundColor() {
        return titleBarBackgroundColor;
    }

    /**
     * @param titleBarBackgroundColor 设置标题栏背景色
     */
    public void setTitleBarBackgroundColor(int titleBarBackgroundColor) {
        this.titleBarBackgroundColor = titleBarBackgroundColor;
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    /**
     * @param titleTextColor 设置标题栏文字颜色
     */
    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public int getNextBtnUnSelectTextColor() {
        return nextBtnUnSelectTextColor;
    }

    /**
     * @param nextBtnUnSelectTextColor 设置下一步按钮未选中颜色
     */
    public void setNextBtnUnSelectTextColor(int nextBtnUnSelectTextColor) {
        this.nextBtnUnSelectTextColor = nextBtnUnSelectTextColor;
    }

    public int getNextBtnSelectedTextColor() {
        return nextBtnSelectedTextColor;
    }

    /**
     * @param nextBtnSelectedTextColor 设置下一步按钮选中颜色
     */
    public void setNextBtnSelectedTextColor(int nextBtnSelectedTextColor) {
        this.nextBtnSelectedTextColor = nextBtnSelectedTextColor;
    }

    public Drawable getNextBtnUnSelectBackground() {
        return nextBtnUnSelectBackground;
    }

    public void setNextBtnUnSelectBackground(Drawable nextBtnUnSelectBackground) {
        this.nextBtnUnSelectBackground = nextBtnUnSelectBackground;
    }

    public Drawable getNextBtnSelectedBackground() {
        return nextBtnSelectedBackground;
    }

    public void setNextBtnSelectedBackground(Drawable nextBtnSelectedBackground) {
        this.nextBtnSelectedBackground = nextBtnSelectedBackground;
    }

    public int getUnSelectIconID() {
        return unSelectIconID;
    }

    /**
     * @param unSelectIconID 设置item未选中时图标
     */
    public void setUnSelectIconID(int unSelectIconID) {
        this.unSelectIconID = unSelectIconID;
    }

    public int getFullIconID() {
        return fullIconID;
    }

    /**
     * @param fullIconID 设置剪裁区域充满图标
     */
    public void setFullIconID(int fullIconID) {
        this.fullIconID = fullIconID;
    }

    public int getFitIconID() {
        return fitIconID;
    }

    /**
     * @param fitIconID 设置剪裁区域自适应图标
     */
    public void setFitIconID(int fitIconID) {
        this.fitIconID = fitIconID;
    }

    public int getGapIconID() {
        return gapIconID;
    }

    /**
     * @param gapIconID 设置留白图标
     */
    public void setGapIconID(int gapIconID) {
        this.gapIconID = gapIconID;
    }

    public int getFillIconID() {
        return FillIconID;
    }

    /**
     * @param fillIconID 设置填充图标
     */
    public void setFillIconID(int fillIconID) {
        FillIconID = fillIconID;
    }
}
