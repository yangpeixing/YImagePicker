package com.ypx.imagepicker.presenter;

import android.widget.ImageView;

import com.ypx.imagepicker.bean.ImageItem;

import java.io.Serializable;

/**
 * Time: 2019/10/27 22:22
 * Author:ypx
 * Description:
 */
 interface BasePresenter extends Serializable {
    /**
     * 加载列表缩略图
     *
     * @param imageView imageView
     * @param item      图片信息
     * @param size      加载尺寸
     */
    void displayListImage(ImageView imageView, ImageItem item, int size);
}
