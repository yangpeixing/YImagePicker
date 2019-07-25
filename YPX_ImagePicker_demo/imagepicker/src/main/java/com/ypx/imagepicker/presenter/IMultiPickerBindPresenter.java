package com.ypx.imagepicker.presenter;

import android.content.Context;
import android.widget.ImageView;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MultiUiConfig;

import java.io.Serializable;

/**
 * 作者：yangpeixing on 2018/9/26 15:46
 * 功能：图片选择器配置接口，由客户端实现，客户端必须实现
 * 产权：南京婚尚信息技术
 */
public interface IMultiPickerBindPresenter extends Serializable {
    /**
     * 加载列表缩略图
     *
     * @param imageView imageView
     * @param url       图片地址
     * @param size      尺寸
     */
    void displayListImage(ImageView imageView, String url, int size);

    /**
     * 加载详情预览图片
     *
     * @param imageView imageView
     * @param url       图片地址
     */
    void displayPerViewImage(ImageView imageView, String url);

    /**
     * 设置ui显示样式
     *
     * @param context 上下文
     * @return MultiUiConfig
     */
    MultiUiConfig getUiConfig(Context context);

    /**
     * 提示
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    void tip(Context context, String msg);

    void onClickVideo(ImageItem videoItem);

}
