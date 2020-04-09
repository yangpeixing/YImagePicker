package com.ypx.imagepicker.views.base;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.presenter.IPickerPresenter;

/**
 * Time: 2019/11/13 14:39
 * Author:ypx
 * Description:自定义文件夹item
 */
public abstract class PickerFolderItemView extends PBaseLayout {

    /**
     * @return 获取每个item的高度，如果自适应返回-1
     */
    public abstract int getItemHeight();

    /**
     * 加载文件夹缩略图
     *
     * @param imageSet  文件夹
     * @param presenter presenter
     */
    public abstract void displayCoverImage(ImageSet imageSet, IPickerPresenter presenter);

    /**
     * 加载item
     *
     * @param imageSet 当前文件夹信息
     */
    public abstract void loadItem(ImageSet imageSet);

    public PickerFolderItemView(Context context) {
        super(context);
    }

    public PickerFolderItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PickerFolderItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
