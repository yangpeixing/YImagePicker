package com.ypx.imagepickerdemo.style;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MultiUiConfig;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.ViewSizeUtils;
import com.ypx.imagepickerdemo.R;

/**
 * Description: 自定义样式
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class CustomImgPickerPresenter implements IMultiPickerBindPresenter {

    @Override
    public void displayListImage(ImageView imageView, String url, int size) {
        if (size == 0) {
            Glide.with(imageView.getContext()).load(url).into(imageView);
        } else {
            Glide.with(imageView.getContext()).load(url).into(imageView);
        }
    }

    @Override
    public void displayPerViewImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

    @Override
    public MultiUiConfig getUiConfig(Context context) {
        MultiUiConfig config = new MultiUiConfig();
        config.setTopBarTitleGravity(Gravity.CENTER);
        config.setShowBottomBar(false);
        Drawable drawable = context.getResources().getDrawable(R.mipmap.picker_arrow_down);
        drawable.setBounds(0, 0, ViewSizeUtils.dp(context, 6), ViewSizeUtils.dp(context, 5));
        config.setTitleDrawableRight(drawable);

        config.setBackIconID(R.mipmap.picker_icon_close_black);
        config.setOkBtnSelectTextColor(context.getResources().getColor(R.color.picker_theme_color));
        config.setOkBtnUnSelectTextColor(Color.parseColor("#50859D7B"));
        config.setOkBtnText("下一步");
        config.setThemeColor(context.getResources().getColor(R.color.picker_theme_color));
        config.setBottomBarBackgroundColor(Color.parseColor("#f0F6F6F6"));
        config.setTopBarBackgroundColor(Color.parseColor("#F6F6F6"));
        config.setPreviewTextColor(context.getResources().getColor(R.color.picker_theme_color));
        return config;
    }

    @Override
    public void tip(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickVideo(ImageItem videoItem) {

    }
}
