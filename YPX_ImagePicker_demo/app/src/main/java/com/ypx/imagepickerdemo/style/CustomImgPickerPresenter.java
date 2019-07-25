package com.ypx.imagepickerdemo.style;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MultiUiConfig;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepickerdemo.R;

/**
 * 作者：yangpeixing on 2018/9/26 15:57
 * 功能：微信样式图片选择器
 * 产权：南京婚尚信息技术
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
        config.setImmersionBar(true);
        config.setThemeColor(Color.parseColor("#ffffff"));
        config.setSelectedIconID(R.mipmap.ypx_pic_selected);
        config.setUnSelectIconID(R.mipmap.ypx_pic_unselected);
        config.setBackIconID(R.mipmap.ypx_icon_back_black);
        config.setCameraIconID(R.mipmap.ypx_ic_camera);
        config.setoKBtnText("完成");
        config.setTitleColor(Color.WHITE);
        config.setTopBarTitleGravity(Gravity.START);
        config.setRightBtnBackground(R.drawable.selector_bt_selectpic);
        config.setTopBarBackgroundColor(Color.parseColor("#FF4081"));
        config.setBottomBarBackgroundColor(Color.parseColor("#FF4081"));
        config.setGridViewBackgroundColor(Color.WHITE);
        config.setImageItemBackgroundColor(Color.parseColor("#50000000"));
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
