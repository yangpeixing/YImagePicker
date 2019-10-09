package com.ypx.imagepicker.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.ypx.imagepicker.bean.CropUiConfig;
import com.ypx.imagepicker.bean.ImageItem;

import java.io.Serializable;

/**
 * Description: 图片加载提供类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public interface ICropPickerBindPresenter extends Serializable {

    void displayListImage(ImageView imageView, ImageItem item, int size);

    void displayCropImage(ImageView imageView, ImageItem item);

    /**
     * 设置ui显示样式
     *
     * @param context 上下文
     * @return PickerUiConfig
     */
    CropUiConfig getUiConfig(Context context);

    void overMaxCountTip(Context context, int maxCount, String defaultTip);

    void clickVideo(Activity activity, ImageItem imageItem);
}
