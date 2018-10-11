package com.ypx.imagepicker.config;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * 作者：yangpeixing on 2018/9/26 15:46
 * 功能：图片选择器配置接口，由客户端实现，客户端必须实现
 * 产权：南京婚尚信息技术
 */
public interface IImgPickerUIConfig extends Serializable {
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
     * 是否沉浸式状态栏，如果返回true将会自动读取topbar的颜色
     *
     * @return 是否
     */
    boolean isImmersionBar();

    /**
     * 获取整个指示器主题颜色，主要是所有按钮颜色
     *
     * @return 颜色
     */
    int getThemeColor();

    /**
     * 获取图片选中图标id
     *
     * @return 选中图标id
     */
    int getSelectedIconID();

    /**
     * 获取图片未选中图标id
     *
     * @return 未选中图标id
     */
    int getUnSelectIconID();

    /**
     * 获取返回图标ID
     *
     * @return 返回图标id
     */
    int getBackIconID();

    /**
     * 获取拍照按钮的图片ID
     *
     * @return id
     */
    int getCameraIconID();

    /**
     * 获取完成按钮的文本，调用者可自定义默认文本为完成或者确定
     *
     * @return 文本
     */
    String getOKBtnText();

    /**
     * 获取标题字体颜色
     *
     * @return 颜色
     */
    int getTitleColor();


    /**
     * 设置标题栏对齐方式
     *
     * @return Gravity
     */
    int getTopBarTitleGravity();

    /**
     * 获取右上角按钮的背景样式，如果没有背景可以返回null
     *
     * @return 背景
     */
    Drawable getRightBtnBackground();

    /**
     * 获取顶部topbar的背景色
     *
     * @return 背景色
     */
    int getTopBarBackgroundColor();

    /**
     * 获取底部BottomBar的背景色
     *
     * @return 背景色
     */
    int getBottomBarBackgroundColor();

    /**
     * gridview的背景色
     *
     * @return 背景色
     */
    int getGridViewBackgroundColor();

    /**
     * item的默认背景色
     *
     * @return 颜色
     */
    int getImageItemBackgroundColor();

    /**
     * 提示
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    void tip(Context context, String msg);

}
