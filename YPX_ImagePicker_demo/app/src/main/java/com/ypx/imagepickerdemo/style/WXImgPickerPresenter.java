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
import com.ypx.imagepicker.utils.CornerUtils;
import com.ypx.imagepicker.utils.ViewSizeUtils;
import com.ypx.imagepickerdemo.R;

/**
 * Description:<br> 多选选择器数据绑定类
 *
 * @author honglin.zhang@yoho.cn<br>
 * @version 1.0.0 <br>
 * @date 2019/2/27 13:49<br>
 */
public class WXImgPickerPresenter implements IMultiPickerBindPresenter {

    @Override
    public void displayListImage(ImageView imageView, String url, int size) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

    @Override
    public void displayPerViewImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

    @Override
    public MultiUiConfig getUiConfig(Context context) {
        MultiUiConfig config = new MultiUiConfig();
        config.setImmersionBar(true);
        config.setThemeColor(Color.parseColor("#09C768"));
        config.setSelectedIconID(R.mipmap.picker_wechat_select);
        config.setUnSelectIconID(R.mipmap.picker_wechat_unselect);

        config.setBackIconID(R.mipmap.picker_icon_back_black);
        config.setBackIconColor(Color.BLACK);
        config.setTopBarBackgroundColor(Color.parseColor("#F1F1F1"));
        config.setTopBarTitleGravity(Gravity.START);
        config.setTitleColor(Color.BLACK);

        int r = ViewSizeUtils.dp(context, 2);
        config.setOkBtnSelectBackground(CornerUtils.cornerDrawable(Color.parseColor("#09C768"), r));
        config.setOkBtnUnSelectBackground(CornerUtils.cornerDrawable(Color.parseColor("#B4ECCE"), r));
        config.setOkBtnSelectTextColor(Color.WHITE);
        config.setOkBtnUnSelectTextColor(Color.parseColor("#50ffffff"));
        config.setOkBtnText("完成");

        config.setPickerBackgroundColor(Color.WHITE);
        config.setPickerItemBackgroundColor(Color.parseColor("#484848"));
        config.setBottomBarBackgroundColor(Color.parseColor("#333333"));
        config.setCameraIconID(R.mipmap.picker_ic_camera);
        config.setCameraBackgroundColor(Color.parseColor("#484848"));
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
