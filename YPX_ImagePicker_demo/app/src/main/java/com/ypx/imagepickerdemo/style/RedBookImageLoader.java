package com.ypx.imagepickerdemo.style;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.ypxredbookpicker.ImageLoaderProvider;
import com.example.ypxredbookpicker.utils.ViewSizeUtils;

/**
 * Description: TODO
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class RedBookImageLoader implements ImageLoaderProvider {
    @Override
    public void displayListImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).asBitmap().into(imageView);
    }

    @Override
    public void displayCropImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).into(imageView);
    }

    @Override
    public View getBottomView(final Context context) {
        TextView textView = new TextView(context);
        textView.setText("这是底部自定义View");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.RED);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewSizeUtils.dp(context, 50)));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "点击了", Toast.LENGTH_SHORT).show();
            }
        });
        return textView;
    }
}
