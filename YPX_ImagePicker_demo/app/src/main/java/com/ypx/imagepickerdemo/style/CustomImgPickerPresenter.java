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
