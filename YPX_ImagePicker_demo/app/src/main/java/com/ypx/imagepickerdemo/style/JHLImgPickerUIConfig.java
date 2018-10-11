package com.ypx.imagepickerdemo.style;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ypx.imagepicker.config.IImgPickerUIConfig;
import com.ypx.imagepickerdemo.R;

/**
 * 作者：yangpeixing on 2018/9/26 15:57
 * 功能：微信样式图片选择器
 * 产权：南京婚尚信息技术
 */
public class JHLImgPickerUIConfig implements IImgPickerUIConfig {

    @Override
    public void displayListImage(ImageView imageView, String url, int size) {
        if (size == 0) {
            Glide.with(imageView.getContext()).load(url).asBitmap().into(imageView);
        } else {
            Glide.with(imageView.getContext()).load(url).asBitmap().override(size, size).into(imageView);
        }
    }

    @Override
    public void displayPerViewImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

    @Override
    public boolean isImmersionBar() {
        return false;
    }

    @Override
    public int getThemeColor() {
        return Color.parseColor("#EA508F");
    }

    @Override
    public int getSelectedIconID() {
        return R.mipmap.ypx_pic_selected;
    }

    @Override
    public int getUnSelectIconID() {
        return R.mipmap.ypx_pic_unselected;
    }

    @Override
    public int getBackIconID() {
        return R.mipmap.ypx_icon_back_black;
    }

    @Override
    public int getCameraIconID() {
        return R.mipmap.ypx_ic_camera;
    }

    @Override
    public String getOKBtnText() {
        return "完成";
    }

    @Override
    public int getTitleColor() {
        return Color.BLACK;
    }

    @Override
    public int getTopBarTitleGravity() {
        return Gravity.CENTER;
    }

    @Override
    public Drawable getRightBtnBackground() {
        return null;
    }

    @Override
    public int getTopBarBackgroundColor() {
        return Color.parseColor("#ffffff");
    }

    @Override
    public int getBottomBarBackgroundColor() {
        return Color.parseColor("#303030");
    }

    @Override
    public int getGridViewBackgroundColor() {
        return Color.WHITE;
    }

    @Override
    public int getImageItemBackgroundColor() {
        //return Color.parseColor("#404040");

        return Color.TRANSPARENT;
    }

    @Override
    public void tip(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
