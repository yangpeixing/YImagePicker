package com.ypx.imagepicker.bean;

/**
 * Description: 图片剪裁模式
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class ImageCropMode {
    /**
     * 填充模式，按照图片宽度填充到容器(屏幕)宽度
     */
    public static int CropViewScale_FULL = -5;
    /**
     * 自适应模式，按照图片高度自适应容器高度
     */
    public static int CropViewScale_FIT = -6;
    /**
     * imageView图片显示模式 填充
     */
    public static int ImageScale_FILL = -7;

    /**
     * imageView图片显示模式 留白
     */
    public static int ImageScale_GAP = -8;
}
