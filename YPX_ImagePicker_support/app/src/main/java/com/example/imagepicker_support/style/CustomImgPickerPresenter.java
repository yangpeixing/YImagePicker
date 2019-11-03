package com.example.imagepicker_support.style;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.imagepicker_support.R;
import com.ypx.imagepicker.adapter.multi.MultiGridAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickConstants;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;

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
        config.setThemeColor(Color.parseColor("#859D7B"));
        //标题栏模式，从标题栏选择相册
        config.setPickStyle(PickerUiConfig.PICK_STYLE_TITLE);

        config.setBackIconID(R.mipmap.picker_icon_close_black);
        config.setTitleBarBackgroundColor(Color.parseColor("#F6F6F6"));
        config.setTitleBarGravity(Gravity.CENTER);
        //设置标题栏选择相册的图标（上下箭头）
        config.setTitleDrawableRight(context.getResources().getDrawable(R.mipmap.picker_arrow_down));

        config.setBottomBarBackgroundColor(Color.parseColor("#f0F6F6F6"));
        //设置预览页面底部文字颜色，防止和背景色撞色看不到文字
        config.setBottomPreviewTextColor(Color.parseColor("#859D7B"));

        config.setOkBtnSelectTextColor(Color.parseColor("#859D7B"));
        config.setOkBtnUnSelectTextColor(Color.parseColor("#50859D7B"));
        config.setOkBtnText("下一步");

        //设置选择器自定义item样式
        config.setPickerItemView(new CustomPickerItem(context));
        return config;
    }

    @Override
    public void tip(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void overMaxCountTip(Context context, int maxCount) {
        tip(context, "最多选择" + maxCount + "个文件");
    }

    @Override
    public boolean interceptPickerCancel(Activity activity, ArrayList<ImageItem> selectedList) {
        return false;
    }

    @Override
    public boolean interceptVideoClick(Activity activity, ImageItem imageItem) {
        return false;
    }

    @NonNull
    @Override
    public PickConstants getPickConstants(Context context) {
        return new PickConstants(context);
    }

    @Override
    public void imageItemClick(Context context, ImageItem imageItem, ArrayList<ImageItem> selectImageList,
                               ArrayList<ImageItem> allSetImageList, MultiGridAdapter adapter) {
        if (selectImageList == null || adapter == null) {
            return;
        }
        tip(context, "我是自定义的图片点击事件");
        adapter.preformCheckItem(imageItem);
    }

}
