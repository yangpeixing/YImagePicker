package com.ypx.imagepickerdemo.style;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.ypx.imagepicker.adapter.multi.MultiGridAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.FileUtil;
import com.ypx.imagepicker.utils.ViewSizeUtils;
import com.ypx.imagepickerdemo.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Description: 自定义样式
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class CustomImgPickerPresenter implements IMultiPickerBindPresenter {

    @Override
    public void displayListImage(final ImageView imageView, final ImageItem item, int size) {
        Glide.with(imageView.getContext()).load(item.path).into(imageView);
    }

    @Override
    public void displayPerViewImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

    @Override
    public PickerUiConfig getUiConfig(Context context) {
        PickerUiConfig config = new PickerUiConfig();
        config.setImmersionBar(true);
        config.setThemeColor(context.getResources().getColor(R.color.picker_theme_color));
        config.setPickStyle(PickerUiConfig.PICK_STYLE_TITLE);

        config.setBackIconID(R.mipmap.picker_icon_close_black);
        config.setTitleBarBackgroundColor(Color.parseColor("#F6F6F6"));
        config.setTitleBarGravity(Gravity.CENTER);
        config.setTitleDrawableRight(context.getResources().getDrawable(R.mipmap.picker_arrow_down));

        config.setBottomBarBackgroundColor(Color.parseColor("#f0F6F6F6"));
        config.setBottomPreviewTextColor(context.getResources().getColor(R.color.picker_theme_color));

        config.setOkBtnSelectTextColor(context.getResources().getColor(R.color.picker_theme_color));
        config.setOkBtnUnSelectTextColor(Color.parseColor("#50859D7B"));
        config.setOkBtnText("下一步");

        config.setPickerItemView(new CustomPickerItem(context));
        return config;
    }

    @Override
    public void tip(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void imageItemClick(Context context, ImageItem imageItem, ArrayList<ImageItem> selectImageList,
                               ArrayList<ImageItem> allSetImageList, MultiGridAdapter adapter) {
        tip(context, "我是自定义的图片点击事件");
    }

}
