package com.example.ypxredbookpicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

/**
 * Description: TODO
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/22
 */
public class ImageEditActivity extends FragmentActivity {
    public static final String INTENT_KEY_URL="CropUrl";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageedit);
        ImageView iv_image = findViewById(R.id.iv_image);
        String url = getIntent().getStringExtra(INTENT_KEY_URL);
        ImageLoaderProvider imageLoader = (ImageLoaderProvider) getIntent().getSerializableExtra(SelectPicAndCropActivity.INTENT_KEY);
        if (imageLoader != null) {
            imageLoader.displayListImage(iv_image, url, 0);
        }
    }
}
