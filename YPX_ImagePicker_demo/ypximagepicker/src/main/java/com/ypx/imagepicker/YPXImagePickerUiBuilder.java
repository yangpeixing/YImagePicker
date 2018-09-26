package com.ypx.imagepicker;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.ypx.imagepicker.interf.PickStyle;

/**
 * 作者：yangpeixing on 2018/4/3 10:37
 * 功能：图片选择器UI构建类
 * 产权：南京婚尚信息技术
 */
public class YPXImagePickerUiBuilder {
    private Context context;
    private int rowCount = 3;
    private int themeColor;
    private Drawable selectIcon;
    private Drawable unSelectIcon;
    private Drawable cameraIcon;
   // private BaseTitleBar titleBar;
    private PickStyle pickStyle;

    public YPXImagePickerUiBuilder(Context context) {
        this.context = context;
        if (context != null) {
            cameraIcon = context.getResources().getDrawable(R.mipmap.ypx_ic_camera);
            themeColor = context.getResources().getColor(R.color.main_color);
            selectIcon = context.getResources().getDrawable(R.mipmap.ypx_pic_selected);
            unSelectIcon = context.getResources().getDrawable(R.mipmap.ypx_pic_unselected);
            //titleBar = new DefaultTitleBar(context);
            pickStyle = PickStyle.Bottom;
        }
    }

    public YPXImagePickerUiBuilder(Context context, int rowCount, int themeColor, Drawable selectIcon,
                                   Drawable unSelectIcon, PickStyle pickStyle) {
        this.context = context;
        this.rowCount = rowCount;
        this.themeColor = themeColor;
        this.selectIcon = selectIcon;
        this.unSelectIcon = unSelectIcon;
       // this.titleBar = titleBar;
        this.pickStyle = pickStyle;
    }

    public YPXImagePickerUiBuilder withRowCount(int rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    public YPXImagePickerUiBuilder withPickStyle(PickStyle pickStyle) {
        this.pickStyle = pickStyle;
        return this;
    }

    public YPXImagePickerUiBuilder withThemeColor(int themeColor) {
        this.themeColor = themeColor;
        return this;
    }

    public YPXImagePickerUiBuilder withSelectIcon(Drawable selectIcon) {
        this.selectIcon = selectIcon;
        return this;
    }

    public YPXImagePickerUiBuilder withUnSelectIcon(Drawable unSelectIcon) {
        this.unSelectIcon = unSelectIcon;
        return this;
    }

//    public YPXImagePickerUiBuilder withTitleBar(BaseTitleBar titleBar) {
//        this.titleBar = titleBar;
//        return this;
//    }

    public YPXImagePickerUiBuilder withCameraIcon(Drawable cameraIcon) {
        this.cameraIcon = cameraIcon;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getThemeColor() {
        return themeColor;
    }

    public Drawable getSelectIcon() {
        return selectIcon;
    }

    public Drawable getUnSelectIcon() {
        return unSelectIcon;
    }

//    public BaseTitleBar getTitleBar() {
//        return titleBar;
//    }

    public PickStyle getPickStyle() {
        return pickStyle;
    }

    public Drawable getCameraIcon() {
        return cameraIcon;
    }
}
