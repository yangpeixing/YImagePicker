package com.example.imagepicker_support.style;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.utils.PViewSizeUtils;

/**
 * Description: 小红书样式
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class RedBookCropPresenter implements ICropPickerBindPresenter {
    @Override
    public void displayListImage(ImageView imageView, ImageItem item) {
        Glide.with(imageView.getContext()).load(item.path).into(imageView);
    }

    @Override
    public void displayCropImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

    @Override
    public View getBottomView(final Context context) {
        TextView textView = new TextView(context);
        textView.setText("这是底部自定义View");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.RED);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PViewSizeUtils.dp(context, 50)));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "点击了", Toast.LENGTH_SHORT).show();
            }
        });
        return textView;
    }

    @Override
    public void showDraftDialog(Context context) {

    }

    @Override
    public void clickVideo(Activity activity, ImageItem imageItem, boolean startDirect) {
        Toast.makeText(activity, imageItem.path, Toast.LENGTH_SHORT).show();
    }

}
