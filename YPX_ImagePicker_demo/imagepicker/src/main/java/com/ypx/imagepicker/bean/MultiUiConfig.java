package com.ypx.imagepicker.bean;

import java.io.Serializable;

/**
 * Description: 选择器UI配置项
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class MultiUiConfig implements Serializable {
    /**
     * 是否沉浸式状态栏，如果返回true将会自动读取topbar的颜色
     */
    private boolean isImmersionBar;
    /**
     * 获取整个选择器主题颜色，主要是所有按钮颜色
     */
    private int themeColor;
    /**
     * 获取图片选中图标id
     */
    private int selectedIconID;
    /**
     * 获取图片未选中图标id
     */
    private int unSelectIconID;
    /**
     * 获取返回图标ID
     */
    private int backIconID;
    /**
     * 获取拍照按钮的图片ID
     */
    private int cameraIconID;
    /**
     * 获取完成按钮的文本，调用者可自定义默认文本为完成或者确定
     */
    private String oKBtnText;
    /**
     * 获取标题字体颜色
     */
    private int titleColor;
    /**
     * 设置标题栏对齐方式
     */
    private int topBarTitleGravity;
    /**
     * 获取右上角按钮的背景样式，如果没有背景可以返回null
     */
    private int rightBtnBackgroundId;
    /**
     * 获取顶部topbar的背景色
     */
    private int topBarBackgroundColor;
    /**
     * 获取底部BottomBar的背景色
     */
    private int bottomBarBackgroundColor;
    /**
     * gridview的背景色
     */
    private int gridViewBackgroundColor;
    /**
     * item的默认背景色
     */
    private int imageItemBackgroundColor;

    /**
     * 返回箭头颜色
     */
    private int leftBackIconColor;

    /**
     * 右上角按钮颜色
     */
    private int rightBtnTextColor;

    public int getLeftBackIconColor() {
        return leftBackIconColor;
    }

    public void setLeftBackIconColor(int leftBackIconColor) {
        this.leftBackIconColor = leftBackIconColor;
    }

    public boolean isImmersionBar() {
        return isImmersionBar;
    }

    public void setImmersionBar(boolean immersionBar) {
        isImmersionBar = immersionBar;
    }

    public int getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(int themeColor) {
        this.themeColor = themeColor;
    }

    public int getSelectedIconID() {
        return selectedIconID;
    }

    public void setSelectedIconID(int selectedIconID) {
        this.selectedIconID = selectedIconID;
    }

    public int getUnSelectIconID() {
        return unSelectIconID;
    }

    public void setUnSelectIconID(int unSelectIconID) {
        this.unSelectIconID = unSelectIconID;
    }

    public int getBackIconID() {
        return backIconID;
    }

    public void setBackIconID(int backIconID) {
        this.backIconID = backIconID;
    }

    public int getCameraIconID() {
        return cameraIconID;
    }

    public void setCameraIconID(int cameraIconID) {
        this.cameraIconID = cameraIconID;
    }

    public String getoKBtnText() {
        return oKBtnText;
    }

    public void setoKBtnText(String oKBtnText) {
        this.oKBtnText = oKBtnText;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public int getTopBarTitleGravity() {
        return topBarTitleGravity;
    }

    public void setTopBarTitleGravity(int topBarTitleGravity) {
        this.topBarTitleGravity = topBarTitleGravity;
    }

    public int getRightBtnBackground() {
        return rightBtnBackgroundId;
    }

    public void setRightBtnBackground(int rightBtnBackground) {
        this.rightBtnBackgroundId = rightBtnBackground;
    }

    public int getTopBarBackgroundColor() {
        return topBarBackgroundColor;
    }

    public void setTopBarBackgroundColor(int topBarBackgroundColor) {
        this.topBarBackgroundColor = topBarBackgroundColor;
    }

    public int getBottomBarBackgroundColor() {
        return bottomBarBackgroundColor;
    }

    public void setBottomBarBackgroundColor(int bottomBarBackgroundColor) {
        this.bottomBarBackgroundColor = bottomBarBackgroundColor;
    }

    public int getGridViewBackgroundColor() {
        return gridViewBackgroundColor;
    }

    public void setGridViewBackgroundColor(int gridViewBackgroundColor) {
        this.gridViewBackgroundColor = gridViewBackgroundColor;
    }

    public int getImageItemBackgroundColor() {
        return imageItemBackgroundColor;
    }

    public void setImageItemBackgroundColor(int imageItemBackgroundColor) {
        this.imageItemBackgroundColor = imageItemBackgroundColor;
    }

    public int getRightBtnTextColor() {
        return rightBtnTextColor;
    }

    public void setRightBtnTextColor(int rightBtnTextColor) {
        this.rightBtnTextColor = rightBtnTextColor;
    }
}
