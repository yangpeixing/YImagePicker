package com.ypx.imagepicker.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 作者：yangpeixing on 2018/4/11 09:51
 * 功能：标题栏基类，用于自定义标题栏，需要用户继承它并实现三个抽象方法
 * 产权：南京婚尚信息技术
 */
public abstract class BaseTitleBar extends LinearLayout {
    protected LinearLayout rootView;
    private Context context;

    public BaseTitleBar(Context context) {
        this(context, null);
    }

    public BaseTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        rootView = (LinearLayout) LayoutInflater.from(context).inflate(getLayoutId(), this);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        findView();
    }

    protected abstract int getLayoutId();

    /**
     * 初始化布局
     */
    protected abstract void findView();

    public abstract TextView getTitleTextView();

    public abstract TextView getCompleteTextView();

    public abstract ImageView getLeftIconImageView();

    public int dp(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, this.getResources().getDisplayMetrics());
    }
}
