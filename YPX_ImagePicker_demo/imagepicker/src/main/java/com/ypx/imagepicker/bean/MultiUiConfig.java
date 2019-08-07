package com.ypx.imagepicker.bean;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.ypx.imagepicker.R;

import java.io.Serializable;

/**
 * Description: 选择器UI样式模型
 * <p>
 * Author: peixing.yang
 * Date: 2018/10/16 11:00
 */
public class MultiUiConfig implements Serializable {
    /**
     * 是否沉浸式状态栏，如果返回true将会自动读取topbar的颜色
     */
    private boolean isImmersionBar = true;
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
     * 返回箭头颜色
     */
    private int backIconColor;

    /**
     * 获取拍照按钮的图片ID
     */
    private int cameraIconID;

    /**
     * 获取拍照按钮的背景色
     */
    private int cameraBackgroundColor;
    /**
     * 获取完成按钮的文本，调用者可自定义默认文本为完成或者确定
     */
    private String OkBtnText;
    /**
     * 获取标题字体颜色
     */
    private int titleColor;
    /**
     * 设置标题栏对齐方式
     */
    private int topBarTitleGravity;
    /**
     * 获取顶部topbar的背景色
     */
    private int topBarBackgroundColor;
    /**
     * 获取底部BottomBar的背景色
     */
    private int bottomBarBackgroundColor;
    /**
     * 选择器的背景色
     */
    private int pickerBackgroundColor;
    /**
     * item的默认背景色
     */
    private int pickerItemBackgroundColor;


    /**
     * 右上角按钮选中颜色
     */
    private int OkBtnSelectTextColor;

    /**
     * 右上角按钮未选中颜色
     */
    private int OkBtnUnSelectTextColor;

    /**
     * 获取右上角按钮选中的背景样式
     */
    private Drawable OkBtnSelectBackground;
    /**
     * 获取右上角按钮未选中的背景样式
     */
    private Drawable OkBtnUnSelectBackground;

    /**
     * 是否显示底部栏，如果不显示底部栏，默认切换相册在标题栏上
     */
    private boolean isShowBottomBar = true;

    /**
     * 标题栏文字右边icon
     */
    private Drawable titleDrawableRight;

    /**
     * 预览文字颜色
     */
    private int previewTextColor;

    public int getBackIconColor() {
        return backIconColor;
    }

    public void setBackIconColor(int leftBackIconColor) {
        this.backIconColor = leftBackIconColor;
    }

    public boolean isImmersionBar() {
        return isImmersionBar;
    }

    public void setImmersionBar(boolean immersionBar) {
        isImmersionBar = immersionBar;
    }

    public boolean isShowBottomBar() {
        return isShowBottomBar;
    }

    public void setShowBottomBar(boolean showBottomBar) {
        isShowBottomBar = showBottomBar;
    }

    public Drawable getTitleDrawableRight() {
        return titleDrawableRight;
    }

    public void setTitleDrawableRight(Drawable titleDrawableRight) {
        this.titleDrawableRight = titleDrawableRight;
    }

    public int getPreviewTextColor() {
        if (previewTextColor == 0) {
            return Color.WHITE;
        }
        return previewTextColor;
    }

    public void setPreviewTextColor(int previewTextColor) {
        this.previewTextColor = previewTextColor;
    }

    public int getThemeColor() {
        if (themeColor == 0) {
            return Color.parseColor("#333333");
        }
        return themeColor;
    }

    public void setThemeColor(int themeColor) {
        this.themeColor = themeColor;
    }

    public int getSelectedIconID() {
        if (selectedIconID == 0) {
            return R.mipmap.picker_wechat_select;
        }
        return selectedIconID;
    }

    public void setSelectedIconID(int selectedIconID) {
        this.selectedIconID = selectedIconID;
    }

    public int getUnSelectIconID() {
        if (unSelectIconID == 0) {
            return R.mipmap.picker_wechat_unselect;
        }
        return unSelectIconID;
    }

    public void setUnSelectIconID(int unSelectIconID) {
        this.unSelectIconID = unSelectIconID;
    }

    public int getBackIconID() {
        if (backIconID == 0) {
            return R.mipmap.picker_icon_back_black;
        }
        return backIconID;
    }

    public void setBackIconID(int backIconID) {
        this.backIconID = backIconID;
    }

    public int getCameraIconID() {
        if (cameraIconID == 0) {
            return R.mipmap.picker_ic_camera;
        }
        return cameraIconID;
    }

    public void setCameraIconID(int cameraIconID) {
        this.cameraIconID = cameraIconID;
    }

    public String getOkBtnText() {
        if (OkBtnText == null) {
            return "完成";
        }
        return OkBtnText;
    }

    public void setOkBtnText(String oKBtnText) {
        this.OkBtnText = oKBtnText;
    }

    public int getTitleColor() {
        if (titleColor == 0) {
            return Color.BLACK;
        }
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

    public int getTopBarBackgroundColor() {
        if (topBarBackgroundColor == 0) {
            return Color.WHITE;
        }
        return topBarBackgroundColor;
    }

    public void setTopBarBackgroundColor(int topBarBackgroundColor) {
        this.topBarBackgroundColor = topBarBackgroundColor;
    }

    public int getBottomBarBackgroundColor() {
        if (bottomBarBackgroundColor == 0) {
            return getThemeColor();
        }
        return bottomBarBackgroundColor;
    }

    public void setBottomBarBackgroundColor(int bottomBarBackgroundColor) {
        this.bottomBarBackgroundColor = bottomBarBackgroundColor;
    }

    public int getPickerBackgroundColor() {
        if (pickerBackgroundColor == 0) {
            return Color.WHITE;
        }
        return pickerBackgroundColor;
    }

    public void setPickerBackgroundColor(int pickerBackgroundColor) {
        this.pickerBackgroundColor = pickerBackgroundColor;
    }

    public int getPickerItemBackgroundColor() {
        if (pickerItemBackgroundColor == 0) {
            return Color.parseColor("#484848");
        }
        return pickerItemBackgroundColor;
    }

    public void setPickerItemBackgroundColor(int pickerItemBackgroundColor) {
        this.pickerItemBackgroundColor = pickerItemBackgroundColor;
    }

    public int getOkBtnSelectTextColor() {
        if (OkBtnSelectTextColor == 0) {
            return Color.WHITE;
        }
        return OkBtnSelectTextColor;
    }

    public void setOkBtnSelectTextColor(int okBtnSelectTextColor) {
        OkBtnSelectTextColor = okBtnSelectTextColor;
    }

    public int getOkBtnUnSelectTextColor() {
        if (OkBtnUnSelectTextColor == 0) {
            return Color.parseColor("#50ffffff");
        }
        return OkBtnUnSelectTextColor;
    }

    public void setOkBtnUnSelectTextColor(int okBtnUnSelectTextColor) {
        OkBtnUnSelectTextColor = okBtnUnSelectTextColor;
    }

    public Drawable getOkBtnSelectBackground() {
        return OkBtnSelectBackground;
    }

    public void setOkBtnSelectBackground(Drawable okBtnSelectBackground) {
        OkBtnSelectBackground = okBtnSelectBackground;
    }

    public Drawable getOkBtnUnSelectBackground() {
        return OkBtnUnSelectBackground;
    }

    public void setOkBtnUnSelectBackground(Drawable okBtnUnSelectBackground) {
        OkBtnUnSelectBackground = okBtnUnSelectBackground;
    }

    public int getCameraBackgroundColor() {
        return cameraBackgroundColor;
    }

    public void setCameraBackgroundColor(int cameraBackgroundColor) {
        this.cameraBackgroundColor = cameraBackgroundColor;
    }
}
