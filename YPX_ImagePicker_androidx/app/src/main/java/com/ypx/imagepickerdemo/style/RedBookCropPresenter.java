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

    @Override
    public void displayCropImage(ImageView imageView, ImageItem item) {
        Glide.with(imageView.getContext()).load(item.path)
                .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                .into(imageView);
    }

    @Override
    public CropUiConfig getUiConfig(Context context) {
        CropUiConfig config = new CropUiConfig();

        config.setUnSelectIconID(R.mipmap.picker_icon_unselect);
        config.setCameraIconID(R.mipmap.picker_ic_camera);
        config.setBackIconID(R.mipmap.picker_icon_close_black);
        config.setFitIconID(R.mipmap.picker_icon_fit);
        config.setFullIconID(R.mipmap.picker_icon_full);
        config.setGapIconID(R.mipmap.picker_icon_haswhite);
        config.setFillIconID(R.mipmap.picker_icon_fill);

        config.setBackIconColor(Color.WHITE);
        config.setCropViewBackgroundColor(Color.parseColor("#222222"));
        config.setCameraBackgroundColor(Color.BLACK);
        config.setThemeColor(Color.RED);
        config.setTitleBarBackgroundColor(Color.BLACK);
        config.setNextBtnSelectedTextColor(Color.RED);
        config.setNextBtnUnSelectTextColor(Color.parseColor("#B0B0B0"));
        config.setTitleTextColor(Color.WHITE);
        config.setGridBackgroundColor(Color.BLACK);
        return config;
    }

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

    @Override
    public void clickVideo(Activity activity, ImageItem imageItem) {
        Toast.makeText(activity, imageItem.path, Toast.LENGTH_SHORT).show();
    }
}
