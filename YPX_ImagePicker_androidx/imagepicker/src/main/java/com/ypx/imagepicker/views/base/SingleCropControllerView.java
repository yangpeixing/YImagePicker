package com.ypx.imagepicker.views.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.ypx.imagepicker.widget.cropimage.CropImageView;

/**
 * Time: 2019/11/13 14:39
 * Author:ypx
 * Description:自定义剪裁页面
 */
public abstract class SingleCropControllerView extends PBaseLayout {

    /**
     * 设置状态栏
     */
    public abstract void setStatusBar();

    /**
     * @return 获取可以点击完成的View
     */
    public abstract View getCompleteView();

    /**
     * @param cropImageView 剪裁的ImageView
     * @param params        params
     */
    public abstract void setCropViewParams(CropImageView cropImageView, MarginLayoutParams params);

    public SingleCropControllerView(Context context) {
        super(context);
    }

    public SingleCropControllerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleCropControllerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
