package com.ypx.imagepicker.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickConstants;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Time: 2019/10/27 22:22
 * Author:ypx
 * Description: 选择器交互基类
 */
public interface BasePresenter extends Serializable {
    /**
     * 加载列表缩略图
     *
     * @param imageView imageView
     * @param item      图片信息
     * @param size      加载尺寸
     */
    void displayListImage(ImageView imageView, ImageItem item, int size);

    /**
     * 提示
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    void tip(Context context, String msg);

    /**
     * 选择超过数量限制提示
     *
     * @param context  上下文
     * @param maxCount 最大数量
     */
    void overMaxCountTip(Context context, int maxCount);

    /**
     * 拦截选择器取消操作，用于弹出二次确认框
     *
     * @param activity     当前选择器页面
     * @param selectedList 当前已经选择的文件列表
     * @return true:则拦截选择器取消， false，不处理选择器取消操作
     */
    boolean interceptPickerCancel(Activity activity, ArrayList<ImageItem> selectedList);

    /**
     * 满足selectConfig.isVideoSinglePick()==true 时，才会触发此方法
     * 在单选视频里，点击视频item会触发此方法
     *
     * @param activity  页面
     * @param imageItem 当前选中视频
     * @return true:则拦截外部回调，直接执行该方法， false，不处理视频点击
     */
    boolean interceptVideoClick(Activity activity, ImageItem imageItem);

    /**
     * 动态配置提示文本
     *
     * @param context context
     * @return PickConstants
     */
    @NonNull
    PickConstants getPickConstants(Context context);
}
