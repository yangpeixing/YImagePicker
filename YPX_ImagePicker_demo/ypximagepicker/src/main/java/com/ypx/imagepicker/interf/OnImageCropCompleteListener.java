package com.ypx.imagepicker.interf;

import android.graphics.Bitmap;

/**
 * 作者：yangpeixing on 2018/4/3 10:49
 * 功能：图片剪裁回调
 * 产权：南京婚尚信息技术
 */
public interface OnImageCropCompleteListener {
    void onImageCropComplete(String url, Bitmap bmp, float ratio);
}
