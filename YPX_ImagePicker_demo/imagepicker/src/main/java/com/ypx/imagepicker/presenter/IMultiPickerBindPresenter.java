package com.ypx.imagepicker.presenter;

import android.content.Context;
import android.widget.ImageView;

import com.ypx.imagepicker.adapter.multi.MultiGridAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerUiConfig;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Description: 图片选择器配置接口，由客户端实现，客户端必须实现
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public interface IMultiPickerBindPresenter extends Serializable {
    /**
     * 加载列表缩略图
     *
     * @param imageView imageView
     * @param item      图片信息
     * @param size      加载尺寸
     */
    void displayListImage(ImageView imageView, ImageItem item, int size);

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
     * @return PickerUiConfig
     */
    PickerUiConfig getUiConfig(Context context);

    /**
     * 提示
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    void tip(Context context, String msg);

    /**
     * 图片点击事件
     *
     * @param context         上下文
     * @param imageItem       当前图片
     * @param selectImageList 当前选中列表
     * @param allSetImageList 当前文件夹所有图片
     * @param adapter         当前列表适配器，用于刷新数据
     *                        <p>
     *                        该方法只有在setPreview(false)的时候才会调用，默认点击图片会跳转预览页面。如果指定了剪裁模式，则不走该方法
     */
    void imageItemClick(Context context, ImageItem imageItem, ArrayList<ImageItem> selectImageList,
                        ArrayList<ImageItem> allSetImageList, MultiGridAdapter adapter);

}
