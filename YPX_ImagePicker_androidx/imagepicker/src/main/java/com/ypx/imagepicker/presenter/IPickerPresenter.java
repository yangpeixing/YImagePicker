package com.ypx.imagepicker.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ypx.imagepicker.adapter.PickerItemAdapter;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.ProgressSceneEnum;
import com.ypx.imagepicker.data.ICameraExecutor;
import com.ypx.imagepicker.data.IReloadExecutor;
import com.ypx.imagepicker.views.PickerUiConfig;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Time: 2019/10/27 22:22
 * Author:ypx
 * Description: 选择器交互基类
 */
public interface IPickerPresenter extends Serializable {
    /**
     * 加载列表缩略图
     *
     * @param view imageView
     * @param item 图片信息
     * @param size 加载尺寸
     */
    void displayImage(View view, ImageItem item, int size, boolean isThumbnail);

    /**
     * 设置自定义ui显示样式
     *
     * @param context 上下文
     * @return PickerUiConfig
     */
    @NonNull
    PickerUiConfig getUiConfig(@Nullable Context context);

    /**
     * 提示
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    void tip(@Nullable Context context, String msg);

    /**
     * 选择超过数量限制提示
     *
     * @param context  上下文
     * @param maxCount 最大数量
     */
    void overMaxCountTip(@Nullable Context context, int maxCount);


    /**
     * 显示loading加载框，注意需要调用show方法
     *
     * @param activity          启动对话框的activity
     * @param progressSceneEnum {@link ProgressSceneEnum}
     *                          </p>
     *                          当progressSceneEnum==当ProgressSceneEnum.loadMediaItem 时，代表在加载媒体文件时显示加载框
     *                          目前框架内规定，当文件夹内媒体文件少于1000时，强制不显示加载框，大于1000时才会执行此方法
     *
     *                          </p>
     *                          当progressSceneEnum==当ProgressSceneEnum.crop 时，代表是剪裁页面的加载框
     * @return DialogInterface 对象，用于关闭加载框，返回null代表不显示加载框
     */
    DialogInterface showProgressDialog(@Nullable Activity activity, ProgressSceneEnum progressSceneEnum);

    /**
     * 拦截选择器完成按钮点击事件
     *
     * @param activity     当前选择器activity
     * @param selectedList 已选中的列表
     * @return true:则拦截选择器完成回调， false，执行默认的选择器回调
     */
    boolean interceptPickerCompleteClick(@Nullable Activity activity, ArrayList<ImageItem> selectedList, BaseSelectConfig selectConfig);

    /**
     * 拦截选择器取消操作，用于弹出二次确认框
     *
     * @param activity     当前选择器页面
     * @param selectedList 当前已经选择的文件列表
     * @return true:则拦截选择器取消， false，不处理选择器取消操作
     */
    boolean interceptPickerCancel(@Nullable Activity activity, ArrayList<ImageItem> selectedList);

    /**
     * <p>
     * 图片点击事件拦截，如果返回true，则不会执行选中操纵，如果要拦截此事件并且要执行选中
     * 请调用如下代码：
     * <p>
     * adapter.preformCheckItem()
     * <p>
     * <p>
     * 此方法可以用来跳转到任意一个页面，比如自定义的预览
     *
     * @param activity        上下文
     * @param imageItem       当前图片
     * @param selectImageList 当前选中列表
     * @param allSetImageList 当前文件夹所有图片
     * @param adapter         当前列表适配器，用于刷新数据
     * @return 是否拦截
     */
    boolean interceptItemClick(@Nullable Activity activity, ImageItem imageItem, ArrayList<ImageItem> selectImageList,
                               ArrayList<ImageItem> allSetImageList, BaseSelectConfig selectConfig, PickerItemAdapter adapter,
                               @Nullable IReloadExecutor reloadExecutor);


    /**
     * 拍照点击事件拦截
     *
     * @param activity  当前activity
     * @param takePhoto 拍照接口
     * @return 是否拦截
     */
    boolean interceptCameraClick(@Nullable Activity activity, ICameraExecutor takePhoto);
}
