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

    /**
     * 加载剪裁区域里的图片
     *
     * @param imageView imageView
     * @param item 当前图片信息
     */
    void displayCropImage(ImageView imageView, ImageItem item);

    /**
     * 设置自定义ui显示样式
     *
     * @param context 上下文
     * @return PickerUiConfig
     */
    CropUiConfig getUiConfig(Context context);

    /**
     * 选择超过数量限制提示
     *
     * @param context    上下文
     * @param maxCount   最大数量
     * @param defaultTip 默认提示文本 “最多选择maxCount张图片”
     */
    void overMaxCountTip(Context context, int maxCount, String defaultTip);

    /**
     * 在单选视频里，点击视频item会触发此回调
     *
     * @param activity  页面
     * @param imageItem 当前选中视频
     */
    void clickVideo(Activity activity, ImageItem imageItem);
}
