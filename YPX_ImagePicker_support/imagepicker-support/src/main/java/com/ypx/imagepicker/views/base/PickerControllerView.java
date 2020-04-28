package com.ypx.imagepicker.views.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import android.support.annotation.NonNull;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;

import java.util.ArrayList;

/**
 * Time: 2019/11/7 13:24
 * Author:ypx
 * Description: 选择器控制类
 */
public abstract class PickerControllerView extends PBaseLayout {

    /**
     * @return 获取当前view的高度
     */
    public abstract int getViewHeight();

    /**
     * @return 获取可以点击触发完成回调的View，如果返回null，则代表不可以触发完成回调
     */
    public abstract View getCanClickToCompleteView();

    /**
     * @return 获取可以跳转到预览的View，如果返回null，则代表不可触发跳转预览
     */
    public abstract View getCanClickToIntentPreviewView();

    /**
     * @return 获取可以切换文件夹列表的View，返回null，则不切换文件夹
     */
    public abstract View getCanClickToToggleFolderListView();

    /**
     * @param title 设置默认标题
     */
    public abstract void setTitle(String title);

    /**
     * 切换文件夹
     *
     * @param isOpen 当前是否是打开文件夹
     */
    public abstract void onTransitImageSet(boolean isOpen);

    /**
     * 切换文件夹回调
     *
     * @param imageSet 当前切换的文件夹
     */
    public abstract void onImageSetSelected(ImageSet imageSet);

    /**
     * 刷新完成按钮状态
     *
     * @param selectedList 已选中列表
     * @param selectConfig 选择器配置项
     */
    public abstract void refreshCompleteViewState(ArrayList<ImageItem> selectedList, BaseSelectConfig selectConfig);

    public boolean isAddInParent() {
        return getViewHeight() > 0;
    }

    public PickerControllerView(Context context) {
        super(context);
    }

    public PickerControllerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PickerControllerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
