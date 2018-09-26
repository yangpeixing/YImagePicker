package com.ypx.imagepicker.interf;

import com.ypx.imagepicker.bean.ImageItem;

/**
 * 作者：yangpeixing on 2018/4/3 10:48
 * 功能：图片选择改变监听器
 * 产权：南京婚尚信息技术
 */
public interface OnImageSelectedChangeListener {
    void onImageSelectChange(int position, ImageItem item, int selectedItemsCount, int maxSelectLimit);
}
