package com.ypx.imagepicker.imp;

import com.ypx.imagepicker.bean.ImageItem;

import java.util.List;

/**
 * 作者：yangpeixing on 2018/4/3 10:49
 * 功能：图片选择完成回调
 * 产权：南京婚尚信息技术
 */
public interface OnImagePickCompleteListener {
    void onImagePickComplete(List<ImageItem> items);
}
