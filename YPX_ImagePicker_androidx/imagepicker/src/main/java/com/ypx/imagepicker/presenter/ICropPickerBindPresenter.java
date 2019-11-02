package com.ypx.imagepicker.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.ypx.imagepicker.bean.CropUiConfig;
import com.ypx.imagepicker.bean.ImageItem;

import java.util.ArrayList;

/**
 * Description: 图片加载提供类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public interface ICropPickerBindPresenter extends BasePresenter {

    /**
     * 加载剪裁区域里的图片
     *
     * @param imageView imageView
     * @param item      当前图片信息
     */
    void displayCropImage(ImageView imageView, ImageItem item);

    /**
     * 设置自定义ui显示样式
     *
     * @param context 上下文
     * @return PickerUiConfig
     */
    CropUiConfig getUiConfig(Context context);
}
