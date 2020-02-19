package com.ypx.imagepicker.views.base;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ypx.imagepicker.ImagePicker;

/**
 * Time: 2019/11/11 14:33
 * Author:ypx
 * Description:所有View的基类，其中包含了dp、getScreenWidth
 */
public abstract class PBaseLayout extends LinearLayout {
    protected View view;

    /**
     * @return item布局id
     */
    protected abstract int getLayoutId();

    /**
     * @param view 初始化view
     */
    protected abstract void initView(View view);

    public PBaseLayout(Context context) {
        super(context);
        init();
    }

    public PBaseLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PBaseLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        view = LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        initView(view);
    }


    protected int dp(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getContext().getResources().getDisplayMetrics());
    }

    protected int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public void onBackPressed() {
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).onBackPressed();
        }
    }

    protected int getThemeColor() {
        return ImagePicker.getThemeColor();
    }
}
