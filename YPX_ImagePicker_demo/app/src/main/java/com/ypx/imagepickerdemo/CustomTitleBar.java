package com.ypx.imagepickerdemo;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.ypx.imagepicker.ui.view.BaseTitleBar;

/**
 * 作者：yangpeixing on 2018/4/11 12:09
 * 功能：
 * 产权：南京婚尚信息技术
 */
public class CustomTitleBar extends BaseTitleBar {
    TextView tv_title_count, btn_ok;
    ImageView btn_backpress;

    public CustomTitleBar(Context context) {
        super(context);
    }

    public CustomTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.custom_titlebar;
    }

    @Override
    protected void findView() {
        tv_title_count = rootView.findViewById(R.id.tv_title_count);
        btn_ok = rootView.findViewById(R.id.btn_ok);
        btn_backpress = rootView.findViewById(R.id.btn_backpress);
        btn_backpress.setColorFilter(Color.WHITE);
    }

    @Override
    public TextView getTitleTextView() {
        return tv_title_count;
    }

    @Override
    public TextView getCompleteTextView() {
        return btn_ok;
    }

    @Override
    public ImageView getLeftIconImageView() {
        return btn_backpress;
    }
}
