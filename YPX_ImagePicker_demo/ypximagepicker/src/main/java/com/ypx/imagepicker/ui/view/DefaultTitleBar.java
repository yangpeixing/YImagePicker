package com.ypx.imagepicker.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.ypx.imagepicker.R;

/**
 * 作者：yangpeixing on 2018/4/11 10:13
 * 功能：默认选择器标题栏样式
 * 产权：南京婚尚信息技术
 */
public class DefaultTitleBar extends BaseTitleBar {
    TextView tv_title_count;
    TextView btn_ok;
    ImageView btn_backpress;

    public DefaultTitleBar(Context context) {
        super(context);
    }

    public DefaultTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ypx_default_titlebar;
    }

    @Override
    protected void findView() {
        tv_title_count = (TextView) rootView.findViewById(R.id.tv_title_count);
        btn_ok = (TextView) rootView.findViewById(R.id.btn_ok);
        btn_backpress = (ImageView) rootView.findViewById(R.id.btn_backpress);
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
