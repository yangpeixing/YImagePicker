package com.ypx.imagepickerdemo.style;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.ypx.imagepicker.bean.CropUiConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.utils.PCornerUtils;
import com.ypx.imagepicker.utils.PViewSizeUtils;
import com.ypx.imagepickerdemo.R;

/**
 * Description: 小红书样式
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class RedBookCropPresenter implements ICropPickerBindPresenter {

    @Override
    public void displayListImage(ImageView imageView, ImageItem item, int size) {
        Glide.with(imageView.getContext()).load(item.path).into(imageView);
    }

    /**
     * 加载剪裁区域里的图片
     *
     * @param imageView imageView
     * @param item      当前图片信息
     */
    @Override
    public void displayCropImage(ImageView imageView, ImageItem item) {
        Glide.with(imageView.getContext()).load(item.path)
                .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                .into(imageView);
    }

    @Override
    public CropUiConfig getUiConfig(Context context) {
        CropUiConfig config = new CropUiConfig();
        //设置主题色，包含选中样式的圆形背景色和边框色
        config.setThemeColor(Color.parseColor("#ff2442"));
        //设置item未选中图标
        config.setUnSelectIconID(R.mipmap.picker_icon_unselect);
        //设置相机图标
        config.setCameraIconID(R.mipmap.picker_ic_camera);
        //设置返回图标
        config.setBackIconID(R.mipmap.picker_icon_close_black);
        //设置剪裁区域自适应图标
        config.setFitIconID(R.mipmap.picker_icon_fit);
        //设置剪裁区域充满图标
        config.setFullIconID(R.mipmap.picker_icon_full);
        //设置留白图标
        config.setGapIconID(R.mipmap.picker_icon_haswhite);
        //设置填充图标
        config.setFillIconID(R.mipmap.picker_icon_fill);
        //设置视频暂停图标
        config.setVideoPauseIconID(R.mipmap.picker_icon_video);
        //设置返回按钮颜色
        config.setBackIconColor(Color.WHITE);
        //设置剪裁区域颜色
        config.setCropViewBackgroundColor(Color.parseColor("#111111"));
        //设置拍照图标背景色
        config.setCameraBackgroundColor(Color.BLACK);
        //设置标题栏背景色
        config.setTitleBarBackgroundColor(Color.BLACK);
        //设置下一步按钮选中文字颜色
        config.setNextBtnSelectedTextColor(Color.WHITE);
        //设置下一步按钮未选中文字颜色
        config.setNextBtnUnSelectTextColor(Color.WHITE);
        //设置标题文字颜色
        config.setTitleTextColor(Color.WHITE);
        //设置item列表背景色
        config.setGridBackgroundColor(Color.BLACK);
        //设置下一步按钮未选中时背景drawable
        config.setNextBtnUnSelectBackground(PCornerUtils.cornerDrawable(Color.parseColor("#50B0B0B0"), PViewSizeUtils.dp(context, 30)));
        //设置下一步按钮选中时背景drawable
        config.setNextBtnSelectedBackground(PCornerUtils.cornerDrawable(Color.parseColor("#ff2442"), PViewSizeUtils.dp(context, 30)));
        //设置是否显示下一步数量提示
        config.setShowNextCount(false);
        //设置下一步按钮文字
        config.setNextBtnText("下一步");
        return config;
    }

    /**
     * 选择超过数量限制提示
     *
     * @param context    上下文
     * @param maxCount   最大数量
     * @param defaultTip 默认提示文本 “最多选择maxCount张图片”
     */
    @Override
    public void overMaxCountTip(Context context, int maxCount, String defaultTip) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(defaultTip);
        builder.setPositiveButton(com.ypx.imagepicker.R.string.picker_str_isee,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 在单选视频里，点击视频item会触发此回调
     *
     * @param activity  页面
     * @param imageItem 当前选中视频
     */
    @Override
    public void clickVideo(Activity activity, ImageItem imageItem) {
        Toast.makeText(activity, imageItem.path, Toast.LENGTH_SHORT).show();
    }
}
