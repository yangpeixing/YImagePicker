package com.example.imagepicker_support.preview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import android.support.annotation.Nullable;

import com.example.imagepicker_support.R;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.utils.PStatusBarUtil;

import java.util.ArrayList;

/**
 * Time: 2019/11/6 18:24
 * Author:ypx
 * Description:简单的多图预览界面，用于演示ImagePicker.closePickerWithCallback(imageItems);
 */
public class PreviewResultListActivity extends Activity {
    ArrayList<ImageItem> imageItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PStatusBarUtil.fullScreen(this);
        setContentView(R.layout.activity_second);
        imageItems = (ArrayList<ImageItem>) getIntent().getSerializableExtra(ImagePicker.INTENT_KEY_PICKER_RESULT);
        ImagesViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setImageViewList(imageItems);
    }

    public void click(View view) {
        ImagePicker.closePickerWithCallback(imageItems);
        finish();
    }
}
