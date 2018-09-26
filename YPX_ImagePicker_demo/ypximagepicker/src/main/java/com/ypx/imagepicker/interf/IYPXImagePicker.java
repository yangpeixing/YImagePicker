package com.ypx.imagepicker.interf;

import android.widget.ImageView;

import com.ypx.imagepicker.YPXImagePickerUiBuilder;

/**
 * 作者：yangpeixing on 2018/6/20 15:39
 * 功能：
 * 产权：南京婚尚信息技术
 */
public interface IYPXImagePicker {
    YPXImagePickerUiBuilder getUiBuilder();

    void loadImage(ImageView imageView, String url, int placeHolder);
}
