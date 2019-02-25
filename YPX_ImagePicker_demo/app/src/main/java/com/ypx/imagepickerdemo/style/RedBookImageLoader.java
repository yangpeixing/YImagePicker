package com.ypx.imagepickerdemo.style;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.ypxredbookpicker.ImageLoaderProvider;

/**
 * Description: TODO
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class RedBookImageLoader implements ImageLoaderProvider {
    @Override
    public void displayListImage(ImageView imageView, String url, int size) {
        Glide.with(imageView.getContext()).load(url).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).into(imageView);
    }
}
